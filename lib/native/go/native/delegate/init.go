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
package delegate

import (
	"errors"
	"fmt"
	"strings"
	"syscall"

	"github.com/metacubex/mihomo/component/process"
	"github.com/metacubex/mihomo/log"

	"cfa/native/app"
	"cfa/native/platform"

	"github.com/metacubex/mihomo/component/dialer"
	"github.com/metacubex/mihomo/constant"
)

var errBlocked = errors.New("blocked")

func Init(home, versionName, gitVersion string, platformVersion int) {
	log.Infoln("Init core, home: %s, versionName: %s, gitVersion: %s, platformVersion: %d", home, versionName, gitVersion, platformVersion)
	constant.SetHomeDir(home)
	// gitVersion = ${CURRENT_BRANCH}_${COMMIT_HASH}_${COMPILE_TIME}
	if versions := strings.Split(gitVersion, "_"); len(versions) == 3 {
		constant.Version = fmt.Sprintf("%s-%s-%s", strings.ToLower(versions[0]), versions[1], strings.ToLower(versionName))
		constant.BuildTime = versions[2]
	} else {
		constant.Version = gitVersion
	}
	constant.Version = strings.ToLower(constant.Version)
	app.ApplyVersionName(versionName)
	app.ApplyPlatformVersion(platformVersion)

	packageNameResolver := func(metadata *constant.Metadata, force bool) (string, error) {
		src, dst := app.MetadataSocketAddrs(metadata)

		if src == nil || dst == nil {
			return "", process.ErrInvalidNetwork
		}
		if force && app.IsSystemMetadata(metadata) {
			return "", process.ErrNotFound
		}

		// Regular core lookup respects the negative cache. Rejected-flow attribution and the native
		// enrichment path can force a fresh query because both retry a specific unresolved socket.
		uid := app.QuerySocketUid(src, dst, force)

		// Always stash the UID so the Kotlin-side AppIdentityResolver can fall
		// back to PackageManager.getPackagesForUid() when FindPackageName yields
		// an empty package name.  This is essential for CMFA builds where
		// metadata.Uid is otherwise never populated.
		//
		// UID 0 is authoritative kernel/root ownership, not a miss. On the CMFA path
		// metadata.Uid stays at its zero value, so the tunnel enrichment pass records it as a
		// terminal system identity instead of retrying forever.
		if uid == 0 {
			app.MarkSystemMetadata(metadata)
		}
		if uid > 0 {
			metadata.Uid = uint32(uid)
		}

		pkg := app.QueryAppByUid(uid)

		log.Debugln("[PKG] %s --> %s by %d[%s]", metadata.SourceAddress(), metadata.RemoteAddress(), uid, pkg)

		if pkg == "" {
			return "", process.ErrNotFound
		}

		return pkg, nil
	}
	process.DefaultPackageNameResolverWithForce = packageNameResolver
	process.DefaultPackageNameResolver = func(metadata *constant.Metadata) (string, error) {
		return packageNameResolver(metadata, false)
	}

	dialer.DefaultSocketHook = func(network, address string, conn syscall.RawConn) error {
		if platform.ShouldBlockConnection() {
			return errBlocked
		}

		return conn.Control(func(fd uintptr) {
			app.MarkSocket(int(fd))
		})
	}
}
