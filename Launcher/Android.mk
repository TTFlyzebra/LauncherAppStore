ifneq ($(JAC_PRODUCT_UI_APX),AP2)
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
LOCAL_PREBUILT_STATIC_JAVA_LIBRARIES :=  gson-2.8.5:libs/gson-2.8.5.jar \
										 glide-3.7.0:libs/glide-3.7.0.jar
include $(BUILD_MULTI_PREBUILT)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_MANIFEST_FILE := src/main/AndroidManifest.xml
LOCAL_SRC_FILES := $(call all-java-files-under, src/main/java)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/src/main/res
LOCAL_ASSET_DIR:= $(LOCAL_PATH)/src/main/assets$(JAC_PRODUCT_UI_APX)
LOCAL_JAVA_LIBRARIES := framework
LOCAL_STATIC_JAVA_LIBRARIES :=  gson-2.8.5 \
								glide-3.7.0 \
								android-support-v4 \
								android-support-v7-appcompat
LOCAL_OVERRIDES_PACKAGES := Home Launcher Launcher2
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PACKAGE_NAME := JAC-Launcher
LOCAL_CERTIFICATE := platform
LOCAL_PROGUARD_ENABLED := disabled
include $(BUILD_PACKAGE)
endif
