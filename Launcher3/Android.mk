ifneq ($(JAC_PRODUCT_UI_APX),AP2)
LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)
include $(BUILD_MULTI_PREBUILT)
include $(CLEAR_VARS)
LOCAL_MODULE_TAGS := optional
LOCAL_MANIFEST_FILE := src/main/AndroidManifest.xml
LOCAL_SRC_FILES := $(call all-java-files-under, src/main/java)
LOCAL_RESOURCE_DIR := $(LOCAL_PATH)/src/$(JAC_PRODUCT_UI_APX)/res \
                      $(LOCAL_PATH)/src/main/res \
                      $(LOCAL_PATH)/../wallpaperpick/src/main/res \
	                  $(LOCAL_PATH)/../../../../../../prebuilts/sdk/current/support/v7/recyclerview/res
LOCAL_JAVA_LIBRARIES := framework \
                        telephony-common
LOCAL_STATIC_JAVA_LIBRARIES := 	android-support-v4 \
								android-support-v7-recyclerview
LOCAL_AAPT_FLAGS := \
    --auto-add-overlay \
    --extra-packages android.support.v7.recyclerview
LOCAL_PRIVILEGED_MODULE := true
LOCAL_PACKAGE_NAME := JAC-Appstore
LOCAL_CERTIFICATE := platform
include $(BUILD_PACKAGE)
endif