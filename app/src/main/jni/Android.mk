LOCAL_PATH := $(call my-dir)

include $(CLEAR_VARS)

LOCAL_MODULE    := processActivity
LOCAL_SRC_FILES := processTools.cpp processing.cpp recnum.cpp Thinning.cpp CString.cpp AddProcess.cpp svm.cpp svm-predict.cpp svm-scale.cpp svm-train.cpp
LOCAL_LDLIBS += -llog
LOCAL_ALLOW_UNDEFINED_SYMBOLS := true
CFLAGS = -g

include $(BUILD_SHARED_LIBRARY)
