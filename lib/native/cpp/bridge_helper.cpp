/*
 * This file is part of MonadBox - A customized edition of YumeBox.
 *
 * MonadBox is free software: you can redistribute it and/or modify
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
 */

#include "bridge_helper.h"

#include <cstdint>

constexpr uint64_t GB_THRESH = 1024ull * 1024ull * 1024ull;
constexpr uint64_t MB_THRESH = 1024ull * 1024ull;
constexpr uint64_t KB_THRESH = 1024ull;
constexpr uint64_t SCALE = 100u;
constexpr uint64_t VAL_MASK = 0x3FFFFFFFull;

uint64_t down_scale_traffic(uint64_t value) noexcept {
  if (value > GB_THRESH)
    return ((value * SCALE / 1024ull / 1024ull / 1024ull) & VAL_MASK) | (3ull << 30);
  if (value > MB_THRESH) return ((value * SCALE / 1024ull / 1024ull) & VAL_MASK) | (2ull << 30);
  if (value > KB_THRESH) return ((value * SCALE / 1024ull) & VAL_MASK) | (1ull << 30);
  return value & VAL_MASK;
}