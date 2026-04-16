/*
 * This file is part of YumeBox.
 *
 * YumeBox is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) YumeLira 2025 - 2026
 */
package tunnel

import (
	"context"
	"sync"
	"time"

	"github.com/metacubex/mihomo/adapter/outboundgroup"
	C "github.com/metacubex/mihomo/constant"
	"github.com/metacubex/mihomo/log"
	"github.com/metacubex/mihomo/tunnel"
)

const defaultTestURL = "https://www.gstatic.com/generate_204"
const healthCheckTimeout = 5 * time.Second

func HealthCheck(name string) {
	log.Infoln("HealthCheck: request group=%s", name)
	p := tunnel.Proxies()[name]

	if p == nil {
		log.Warnln("Request health check for `%s`: not found", name)
		return
	}

	g, ok := p.Adapter().(outboundgroup.ProxyGroup)
	if !ok {
		log.Warnln("Request health check for `%s`: invalid type %s", name, p.Type().String())
		return
	}

	var allProxies []C.Proxy
	for _, pr := range g.Providers() {
		allProxies = append(allProxies, pr.Proxies()...)
	}

	if len(allProxies) == 0 {
		log.Debugln("Health check for `%s`: no proxies found", name)
		return
	}

	log.Infoln("HealthCheck: group=%s proxies=%d", name, len(allProxies))

	wg := &sync.WaitGroup{}
	for _, proxy := range allProxies {
		wg.Add(1)
		go func(px C.Proxy) {
			defer wg.Done()
			ctx, cancel := context.WithTimeout(context.Background(), healthCheckTimeout)
			defer cancel()
			_, _ = px.URLTest(ctx, defaultTestURL, nil)
		}(proxy)
	}
	wg.Wait()
	log.Infoln("HealthCheck: done group=%s", name)
}

func HealthCheckAll() {
	log.Infoln("HealthCheckAll: begin")
	for _, g := range QueryProxyGroupNames(false) {
		go func(group string) {
			HealthCheck(group)
		}(g)
	}
}

func HealthCheckProxy(proxyName string) int {
	p := tunnel.Proxies()[proxyName]
	if p != nil {
		return testProxyDelay(p, proxyName)
	}

	for _, prov := range tunnel.Providers() {
		for _, proxy := range prov.Proxies() {
			if proxy.Name() == proxyName {
				return testProxyDelay(proxy, proxyName)
			}
		}
	}

	log.Warnln("HealthCheckProxy: proxy `%s` not found", proxyName)
	return -1
}

func testProxyDelay(p C.Proxy, proxyName string) int {
	ctx, cancel := context.WithTimeout(context.Background(), healthCheckTimeout)
	defer cancel()

	_, _ = p.URLTest(ctx, defaultTestURL, nil)
	delay := p.LastDelayForTestUrl(defaultTestURL)

	if delay == 0xffff {
		log.Debugln("HealthCheckProxy: proxy=%s, timeout/failed", proxyName)
		return -1
	}

	log.Debugln("HealthCheckProxy: proxy=%s, delay=%dms", proxyName, delay)
	return int(delay)
}
