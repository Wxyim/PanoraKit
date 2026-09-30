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
package app

import (
	"net"
	"net/netip"
	"syscall"

	"cfa/native/platform"

	"github.com/metacubex/mihomo/constant"
)

var markSocketImpl func(fd int)
var querySocketUidImpl func(protocol int, source, target string, force bool) int
var queryPackageNameImpl func(uid int) string

func MarkSocket(fd int) {
	markSocketImpl(fd)
}

// QuerySocketUid resolves the UID that owns the socket [source] → [target].
//
// [force] requests a lookup that must not be answered from the Kotlin-side negative cache. The UID
// enrichment path sets it for connections the core has already seen close: a miss cached while the
// socket was still alive would otherwise mask the one lookup that can still attribute it. The caller
// is responsible for rate limiting a forced lookup.
func QuerySocketUid(source, target net.Addr, force bool) int {
	var protocol int

	switch source.Network() {
	case "udp", "udp4", "udp6":
		protocol = syscall.IPPROTO_UDP
	case "tcp", "tcp4", "tcp6":
		protocol = syscall.IPPROTO_TCP
	default:
		return -1
	}

	if PlatformVersion() < 29 {
		return platform.QuerySocketUidFromProcFs(source, target)
	}

	return querySocketUidImpl(protocol, source.String(), target.String(), force)
}

func ApplyTunContext(markSocket func(fd int), querySocketUid func(int, string, string, bool) int) {
	if markSocket == nil {
		markSocket = func(fd int) {}
	}

	if querySocketUid == nil {
		querySocketUid = func(int, string, string, bool) int { return -1 }
	}
	markSocketImpl = markSocket
	querySocketUidImpl = querySocketUid
}

func ApplyPackageNameResolver(resolver func(int) string) {
	if resolver == nil {
		resolver = func(int) string { return "" }
	}
	queryPackageNameImpl = resolver
}

func QueryPackageName(uid int) string {
	if uid <= 0 {
		return ""
	}
	return queryPackageNameImpl(uid)
}

// MetadataSocketAddrs returns the connection's local and remote socket addresses.
//
// Listeners that carry a name instead of an address (an HTTP/SOCKS inbound records its peer as a
// domain) or that are driven by a per-packet destination (packetaddr) never fill mihomo's raw
// address fields. They are rebuilt from the parsed metadata so those connections stay attributable
// instead of bailing out of the process lookup entirely.
func MetadataSocketAddrs(metadata *constant.Metadata) (source, target net.Addr) {
	return metadataSocketAddr(metadata, true), metadataSocketAddr(metadata, false)
}

func metadataSocketAddr(metadata *constant.Metadata, source bool) net.Addr {
	if source {
		if metadata.RawSrcAddr != nil {
			return metadata.RawSrcAddr
		}
		return metadataAddr(metadata.NetWork, metadata.SrcIP, metadata.SrcPort)
	}
	if metadata.RawDstAddr != nil {
		return metadata.RawDstAddr
	}
	return metadataAddr(metadata.NetWork, metadata.DstIP, metadata.DstPort)
}

func metadataAddr(network constant.NetWork, ip netip.Addr, port uint16) net.Addr {
	if !ip.IsValid() || port == 0 {
		return nil
	}
	addr := ip.Unmap().AsSlice()
	if network == constant.UDP {
		return &net.UDPAddr{IP: addr, Port: int(port)}
	}
	return &net.TCPAddr{IP: addr, Port: int(port)}
}

func init() {
	ApplyTunContext(nil, nil)
	ApplyPackageNameResolver(nil)
}
