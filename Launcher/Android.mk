LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES :=  gson-2.8.5:libs/gson-2.8.5.jar \
										 glide-3.7.0:libs/glide-3.7.0.jar
include $(BUILD_MULTI_PREBUILT)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_SRC_FILES := $(call all-java-files-under, src/main/java)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/src/main/res
LOCAL_JAVA_LIBRARIES := framework
LOCAL_STATIC_JAVA_LIBRARIES :=  gson-2.8.5 \
								glide-3.7.0 \
								android-support-v4 \
								android-support-v7-appcompat
LOCAL_OVERRIDES_PACKAGES := Home Launcher Launcher2
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PACKAGE_NAME := JAC-Launcher
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)
