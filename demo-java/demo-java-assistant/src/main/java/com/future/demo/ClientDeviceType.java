package com.future.demo;

import java.util.*;

/**
 * 客户端设备类型
 */
public enum ClientDeviceType {
    AppleIOS("苹果iOS"),
    AndroidXiaomi("小米"),
    AndroidHuawei("华为"),
    AndroidOppo("Oppo"),
    AndroidVivo("Vivo"),
    AndroidMeizu("Meizu"),
    AndroidSamsung("三星"),
    AndroidUnknown("未知"),
    Web("websocket设备"),
    CloudDrive("网盘客户端"),
    GoogleFcm("Google FCM");

    // 客户端端设备互斥对照
    public static Map<ClientDeviceType, List<ClientDeviceType>> ClientDeviceTypeMutexMap = new HashMap<>();
    static {
        // 1、互斥场景
        // 1.1、不能同时有两个相同的appId+clientId+clientDeviceType
        // 1.2、不能同时有两个移动设备，例如：appId+clientId+AndroidHuawei和appId+clientId+AppleIOS
        // 1.3、不能同时有两个web，例如：appId+clientId+Web
        // 2、共存场景
        // 2.1、能够一个移动设备和一个Web设备同时连接，例如：appId+clientId+AppleIOS和appId+clientId+Web

        // 移动设备互斥
        ArrayList<ClientDeviceType> clientDeviceTypeMutexGroup = new ArrayList<>();
        clientDeviceTypeMutexGroup.addAll(Arrays.asList(AppleIOS, AndroidXiaomi, AndroidHuawei, AndroidOppo, AndroidVivo, AndroidMeizu, AndroidSamsung, AndroidUnknown, GoogleFcm));
        for(ClientDeviceType clientDeviceType : clientDeviceTypeMutexGroup) {
            ClientDeviceTypeMutexMap.put(clientDeviceType, clientDeviceTypeMutexGroup);
        }

        // Web设备互斥
        clientDeviceTypeMutexGroup = new ArrayList<>();
        clientDeviceTypeMutexGroup.addAll(Arrays.asList(Web));
        for(ClientDeviceType clientDeviceType : clientDeviceTypeMutexGroup) {
            ClientDeviceTypeMutexMap.put(clientDeviceType, clientDeviceTypeMutexGroup);
        }
    }

    private String description;

    ClientDeviceType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }
}
