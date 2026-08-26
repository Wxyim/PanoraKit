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
 * Copyright (c) MonadBox Contributors 2026 - Present
 *
 */
package tunnel

import (
	"fmt"
	"strings"

	"github.com/metacubex/mihomo/tunnel"
)

func QueryMode() string {
	return tunnel.Mode().String()
}

// SetMode switches the live tunnel routing mode without reloading the whole
// config. It accepts the same wire names mihomo exposes on its /configs
// endpoint ("rule", "global", "direct").
func SetMode(mode string) error {
	modeKey := strings.ToLower(mode)
	m, ok := tunnel.ModeMapping[modeKey]
	if !ok {
		return fmt.Errorf("unsupported tunnel mode: %s", mode)
	}
	tunnel.SetMode(m)
	return nil
}
