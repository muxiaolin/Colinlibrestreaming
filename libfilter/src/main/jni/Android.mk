LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_SRC_FILES := BlackWhiteFilter.c

LOCAL_MODULE := BlackWhiteFilter

include $(BUILD_SHARED_LIBRARY)