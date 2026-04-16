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

#include "jni_helper.h"

#include <cstdlib>
#include <cstring>
#include <string>

namespace {
jclass c_string = nullptr;

std::string sanitize_utf8(const char* input) {
  if (!input) return std::string();

  std::string out;
  const unsigned char* p = reinterpret_cast<const unsigned char*>(input);

  while (*p != 0) {
    const unsigned char b0 = *p;

    if (b0 < 0x80) {
      out.push_back(static_cast<char>(b0));
      p += 1;
      continue;
    }

    if ((b0 & 0xE0) == 0xC0 && p[1] != 0 && (p[1] & 0xC0) == 0x80) {
      out.push_back(static_cast<char>(p[0]));
      out.push_back(static_cast<char>(p[1]));
      p += 2;
      continue;
    }

    if ((b0 & 0xF0) == 0xE0 && p[1] != 0 && p[2] != 0 && (p[1] & 0xC0) == 0x80 &&
        (p[2] & 0xC0) == 0x80) {
      out.push_back(static_cast<char>(p[0]));
      out.push_back(static_cast<char>(p[1]));
      out.push_back(static_cast<char>(p[2]));
      p += 3;
      continue;
    }

    if ((b0 & 0xF8) == 0xF0 && p[1] != 0 && p[2] != 0 && p[3] != 0 && (p[1] & 0xC0) == 0x80 &&
        (p[2] & 0xC0) == 0x80 && (p[3] & 0xC0) == 0x80) {
      out.push_back(static_cast<char>(p[0]));
      out.push_back(static_cast<char>(p[1]));
      out.push_back(static_cast<char>(p[2]));
      out.push_back(static_cast<char>(p[3]));
      p += 4;
      continue;
    }

    out.push_back('?');
    p += 1;
  }

  return out;
}
}  // namespace

void initialize_jni(JavaVM* vm, JNIEnv* env) {
  jni::initialize_global_vm(vm);

  jclass cls = env->FindClass("java/lang/String");
  c_string = (jclass)env->NewGlobalRef(cls);
  env->DeleteLocalRef(cls);
}

char* jni_get_string(JNIEnv* env, jstring str) {
  if (!env || !str) return nullptr;

  const char* utf = env->GetStringUTFChars(str, nullptr);
  if (!utf) {
    env->ExceptionClear();
    return nullptr;
  }

  char* content = strdup(utf);
  env->ReleaseStringUTFChars(str, utf);
  return content;
}

jstring jni_new_string(JNIEnv* env, const char* str) {
  if (!env || !str) return nullptr;

  jstring result = env->NewStringUTF(str);
  if (result != nullptr || !env->ExceptionCheck()) {
    return result;
  }

  env->ExceptionClear();
  const std::string sanitized = sanitize_utf8(str);
  return env->NewStringUTF(sanitized.c_str());
}

int jni_catch_exception(JNIEnv* env) {
  if (!env) return 0;
  if (env->ExceptionCheck()) {
    env->ExceptionDescribe();
    env->ExceptionClear();
    return 1;
  }
  return 0;
}

void jni_attach_thread(struct _scoped_jni* jni) {
  JavaVM* vm = jni::get_global_vm();
  if (!vm || !jni) return;

  if (vm->GetEnv(reinterpret_cast<void**>(&jni->env), JNI_VERSION_1_6) == JNI_OK) {
    jni->require_release = 0;
    return;
  }

  if (vm->AttachCurrentThread(&jni->env, nullptr) == JNI_OK) {
    jni->require_release = 1;
  }
}

void jni_detach_thread(struct _scoped_jni* jni) {
  if (!jni || !jni->require_release) return;
  JavaVM* vm = jni::get_global_vm();
  if (vm) vm->DetachCurrentThread();
}

void release_string(char** str) {
  if (str && *str) {
    free(*str);
    *str = nullptr;
  }
}