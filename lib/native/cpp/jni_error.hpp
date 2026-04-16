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

#pragma once

#include <jni.h>

#include <cstdint>
#include <expected>
#include <string>
#include <string_view>
#include <utility>

namespace jni {

enum class JniErrorKind : uint8_t {
  None = 0,
  Exception,
  NullPointer,
  ClassNotFound,
  MethodNotFound,
  ThreadAttachFailed,
  OutOfMemory,
};

struct JniError {
  JniErrorKind kind = JniErrorKind::None;
  std::string message;

  JniError() = default;
  JniError(JniErrorKind k, std::string msg) : kind(k), message(std::move(msg)) {}

  [[nodiscard]] explicit operator bool() const noexcept {
    return kind != JniErrorKind::None;
  }

  [[nodiscard]] static JniError capture(JNIEnv* env) {
    if (!env || !env->ExceptionCheck()) return {};
    jthrowable ex = env->ExceptionOccurred();
    env->ExceptionClear();
    if (!ex) return {JniErrorKind::Exception, "Unknown exception"};

    jclass cls = env->FindClass("java/lang/Throwable");
    if (!cls) {
      env->ExceptionClear();
      return {JniErrorKind::Exception, "Exception"};
    }

    jmethodID mid = env->GetMethodID(cls, "getMessage", "()Ljava/lang/String;");
    jstring msgObj = mid ? (jstring)env->CallObjectMethod(ex, mid) : nullptr;

    std::string msg;
    if (msgObj) {
      const char* chars = env->GetStringUTFChars(msgObj, nullptr);
      if (chars) {
        msg = chars;
        env->ReleaseStringUTFChars(msgObj, chars);
      }
      env->DeleteLocalRef(msgObj);
    }
    env->DeleteLocalRef(cls);
    env->DeleteLocalRef(ex);
    return {JniErrorKind::Exception, msg.empty() ? "Exception" : msg};
  }

  [[nodiscard]] static JniError nullPointer(std::string_view ctx = {}) {
    return {JniErrorKind::NullPointer,
            ctx.empty() ? "Null pointer" : std::string("Null: ") + std::string(ctx)};
  }
};

template <typename T>
using JniResult = std::expected<T, JniError>;
using JniVoidResult = std::expected<void, JniError>;

[[nodiscard]] inline JniVoidResult checkException(JNIEnv* env) {
  if (!env) return std::unexpected{JniError::nullPointer("JNIEnv")};
  if (env->ExceptionCheck()) return std::unexpected{JniError::capture(env)};
  return {};
}

template <typename F>
[[nodiscard]] auto safeCall(JNIEnv* env, F&& func) -> JniResult<std::invoke_result_t<F>> {
  using R = std::invoke_result_t<F>;
  if (!env) return std::unexpected{JniError::nullPointer("JNIEnv")};
  R result = std::forward<F>(func)();
  if (env->ExceptionCheck()) return std::unexpected{JniError::capture(env)};
  return result;
}

}  // namespace jni