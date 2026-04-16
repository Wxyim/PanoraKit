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

#include "jni_raii.hpp"

namespace jni {

namespace {
JavaVM* g_vm = nullptr;
}

ScopedJString::ScopedJString(JNIEnv* env, jstring str) {
  if (!env || !str) return;

  jclass cls = env->FindClass("java/lang/String");
  if (!cls) {
    env->ExceptionClear();
    return;
  }

  jmethodID mid = env->GetMethodID(cls, "getBytes", "()[B");
  env->DeleteLocalRef(cls);
  if (!mid) {
    env->ExceptionClear();
    return;
  }

  jbyteArray arr = (jbyteArray)env->CallObjectMethod(str, mid);
  if (!arr) {
    env->ExceptionClear();
    return;
  }

  jsize len = env->GetArrayLength(arr);
  if (len < 0) {
    env->DeleteLocalRef(arr);
    return;
  }

  data_ = std::make_unique<char[]>(len + 1);
  if (!data_) {
    env->DeleteLocalRef(arr);
    return;
  }

  env->GetByteArrayRegion(arr, 0, len, reinterpret_cast<jbyte*>(data_.get()));
  data_[len] = '\0';
  length_ = static_cast<size_t>(len);
  env->DeleteLocalRef(arr);
}

ScopedGlobalRef::ScopedGlobalRef(JNIEnv* env, jobject obj) : env_(env) {
  if (env && obj) ref_ = env->NewGlobalRef(obj);
}

void ScopedGlobalRef::reset() noexcept {
  if (env_ && ref_) {
    env_->DeleteGlobalRef(ref_);
    ref_ = nullptr;
  }
}

ScopedThreadAttachment::ScopedThreadAttachment(JavaVM* vm) : vm_(vm) {
  if (!vm_) return;
  if (vm_->GetEnv(reinterpret_cast<void**>(&env_), JNI_VERSION_1_6) == JNI_OK) {
    attached_ = false;
    return;
  }
  if (vm_->AttachCurrentThread(&env_, nullptr) == JNI_OK) attached_ = true;
}

ScopedThreadAttachment::~ScopedThreadAttachment() {
  if (attached_ && vm_) vm_->DetachCurrentThread();
}

JavaVM* get_global_vm() noexcept {
  return g_vm;
}
void initialize_global_vm(JavaVM* vm) noexcept {
  g_vm = vm;
}

}  // namespace jni