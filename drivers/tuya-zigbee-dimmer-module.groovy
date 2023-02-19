/* 
=============================================================================
Hubitat Elevation Driver for
Tuya Zigbee dimmer modules (1-Gang and 2-Gang)

    https://github.com/matt-hammond-001/hubitat-code

-----------------------------------------------------------------------------
This code is licensed as follows:

BSD 3-Clause License

Copyright (c) 2020, Matt Hammond
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

3. Neither the name of the copyright holder nor the names of its
   contributors may be used to endorse or promote products derived from
   this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
-----------------------------------------------------------------------------
ver 0.2.0  11/12/2021 Matt Hammond - added _TZ3000_7ysdnebc
ver 0.2.1  2021/12/29 kkossev      - added cluster 0003 to the fingerprint, model _TZ3000_7ysdnebc
ver 0.2.2  2022/05/28 kkossev      - moved model and inClusters to modelConfigs, added _TZE200_vm1gyrso 3-Gang Dimmer module
ver 0.2.3  2022/08/30 kkossev      - added TS110E _TZ3210_ngqk6jia fingerprint
ver 0.2.4  2022/09/19 kkossev      - added TS0601 _TZE200_w4cryh2i fingerprint
ver 0.2.5  2022/10/19 kkossev      - TS0601 level control; infoLogging
ver 0.2.6  2022/10/22 kkossev      - importURL to dev. branch; toggle() for TS0601; 'autoOn' for TS0601; level scaling for TS0601; minLevel and maxLevel receive/send for TS0601; bugfixes for TS0601 single EP devices
ver 0.2.7  2022/11/11 kkossev      - added _TZE200_ip2akl4w _TZE200_1agwnems _TZE200_la2c2uo9 _TZE200_579lguh2 _TZE200_fjjbhx9d _TZE200_drs6j6m5; secure the while loops coode when deleting and creating child devices;
ver 0.2.8  2022/11/13 kkossev      - _TZE200_ip2akl4w fingerprint hardcoded
ver 0.2.9  2022/12/10 kkossev      - deleting child devices bug fix; added _TZE200_fvldku9h Tuya Fan Switch; unscheduling old periodic jobs; Tuya Time Sync';
ver 0.2.10 2023/01/02 kkossev      - added _TZE200_e3oitdyu 
ver 0.2.11 2023/02/19 kkossev      - (dev.branch) added TS110E _TZ3210_k1msuvg6; TS0601 _TZE200_r32ctezx fan controller; 
*/

def version() { "0.2.11" }
def timeStamp() {"2023/02/19 8:38 AM"}

import groovy.transform.Field

@Field static final Boolean debug = false
@Field static final Boolean deviceSimulation = false
@Field static final String  simulatedModel = "TS0601"
@Field static final String  simulatedManufacturer = "_TZE200_fvldku9h"

@Field static def modelConfigs = [
    "_TYZB01_v8gtiaed": [ numEps: 2, model: "TS110F", inClusters: "0000,0004,0005,0006,0008",     joinName: "Tuya Zigbee 2-Gang Dimmer module" ],                // '2 gang smart dimmer switch module with neutral'
    "_TYZB01_qezuin6k": [ numEps: 1, model: "TS110F", inClusters: "0000,0004,0005,0006,0008",     joinName: "Tuya Zigbee 1-Gang Dimmer module" ],                // '1 gang smart dimmer switch module with neutral'
    "_TZ3000_ktuoyvt5": [ numEps: 1, model: "TS110F", inClusters: "0000,0004,0005,0006,0008",     joinName: "Tuya Zigbee 1-Gang Switch module" ],                // '1 gang smart        switch module without neutral'
    "_TZ3000_92chsky7": [ numEps: 2, model: "TS110F", inClusters: "0000,0004,0005,0006,0008",     joinName: "Tuya Zigbee 2-Gang Dimmer module (no-neutral)" ],   // '2 gang smart dimmer switch module without neutral'
    "_TZ3000_7ysdnebc": [ numEps: 2, model: "TS110F", inClusters: "0000,0004,0005,0003,0006,0008",joinName: "Tuya 2CH Zigbee dimmer module" ],
    "_TZE200_vm1gyrso": [ numEps: 3, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Tuya Zigbee 3-Gang Dimmer module" ],    
    "_TZE200_whpb9yts": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Tuya Zigbee 1-Gang Dimmer module" ],                // 'Zigbee smart dimmer'
    "_TZE200_ebwgzdqq": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Tuya Zigbee 1-Gang Dimmer module" ],    
    "_TZE200_9i9dt8is": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Tuya Zigbee 1-Gang Dimmer module" ],    
    "_TZE200_dfxkcots": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Tuya Zigbee 1-Gang Dimmer module" ],    
    "gq8b1uv":          [ numEps: 1, model: "gq8b1uv", inClusters: "0000,0004,0005,0006,0008",    joinName: "Tuya Zigbee 1-Gang Dimmer module" ],                 //  TUYATEC Zigbee smart dimmer
    "_TZ3210_ngqk6jia": [ numEps: 2, model: "TS110E", inClusters: "0005,0004,0006,0008,EF00,0000", joinName: "Lonsonho 2-gang Dimmer module"],                    // https://www.aliexpress.com/item/4001279149071.html
    "_TZ3210_zxbtub8r": [ numEps: 1, model: "TS110E", inClusters: "0004,0005,0003,0006,0008,EF00,0000", joinName: "GIRIER Dimmer module 1 ch."],                  // not tested
    "_TZE200_w4cryh2i": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Moes Zigbee Rotary/Touch Light Dimmer" ],             // https://community.hubitat.com/t/moes-zigbee-dimmer-touch/101195 
    "_TZE200_ip2akl4w": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Moes Zigbee 1-Gang Dimmer module" ],                  // https://community.hubitat.com/t/tuya-moes-1-2-3-gang-dimmer/104596/5?u=kkossev 
    "_TZE200_1agwnems": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Moes Zigbee 1-Gang Dimmer module" ],                  // not tested
    "_TZE200_la2c2uo9": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Moes Zigbee 1-Gang Dimmer module" ],                  // not tested
    "_TZE200_579lguh2": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Moes Zigbee 1-Gang Dimmer module" ],                  // not tested
    "_TZE200_fjjbhx9d": [ numEps: 2, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Moes Zigbee 2-Gang Dimmer module" ],                  // https://community.hubitat.com/t/tuya-moes-1-2-3-gang-dimmer/104596/5?u=kkossev 
    "_TZE200_drs6j6m5": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Lifud Model LF-AAZ030-0750-42" ],                     // https://community.hubitat.com/t/tuya-moes-1-2-3-gang-dimmer/104596/25?u=kkossev
    "_TZE200_fvldku9h": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Tuya Fan Switch" ] ,                                  // https://www.aliexpress.com/item/4001242513879.html
    "_TZE200_r32ctezx": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",          joinName: "Tuya Fan Switch" ],                                   // https://github.com/Koenkk/zigbee2mqtt/issues/12793
    "_TZE200_e3oitdyu": [ numEps: 2, model: "TS110E", inClusters: "0000,0004,0005,EF00",          joinName: "Moes ZigBee Dimmer Switche 2CH"],                     // https://community.hubitat.com/t/moes-dimmer-module-2ch/110512 
    "_TZ3210_k1msuvg6": [ numEps: 1, model: "TS110E", inClusters: "0004,0005,0003,0006,0008,EF00,0000", joinName: "Tuya Zigbee 1-Gang Dimmer module"]               // https://community.hubitat.com/t/girier-tuya-zigbee-3-0-light-switch-module-smart-diy-breaker-1-2-3-4-gang-supports-2-way-control/104546/36?u=kkossev
]
    
def config() {
    return modelConfigs[device.getDataValue("manufacturer")]
}

def isTS0601() {
    if (isParent()) {
        return device.getDataValue('model') == "TS0601"
    }
    else {
        return parent?.device.getDataValue('model') == "TS0601"    
    }
}


metadata {
    definition (
        name: "Tuya Zigbee dimmer module",
        namespace: "matthammonddotorg",
        author: "Matt Hammond",
        description: "Driver for Tuya zigbee dimmer modules",
        documentationLink: "https://github.com/matt-hammond-001/hubitat-code/blob/master/drivers/tuya-zigbee-dimmer-module.README.md",
        importUrl: "https://raw.githubusercontent.com/kkossev/hubitat-matt-hammond-fork/master/drivers/tuya-zigbee-dimmer-module.groovy"
    ) {

        capability "Configuration"
        capability "Refresh"
        capability "Light"
        capability "Switch"
        capability "SwitchLevel"
        
        command "toggle"
        
        if (debug == true) {
            command "zTest", [
                [name:"dpCommand", type: "STRING", description: "Tuya DP Command", constraints: ["STRING"]],
                [name:"dpValue",   type: "STRING", description: "Tuya DP value", constraints: ["STRING"]],
                [name:"dpType",    type: "ENUM",   constraints: ["DP_TYPE_VALUE", "DP_TYPE_BOOL", "DP_TYPE_ENUM"], description: "DP data type"] 
            ]
        }
        
        fingerprint profileId:"0104", endpointId:"01", inClusters:"0004,0005,EF00,0000", outClusters:"0019,000A", model:"TS0601", manufacturer:"_TZE200_ip2akl4w"
        
        modelConfigs.each{ data ->
            fingerprint profileId: "0104",
                inClusters: data.value.inClusters,
                outClusters:"0019,000A",
                model: data.value.model,
                manufacturer: data.key,
                deviceJoinName: data.value.joinName
        }
    }
    
    preferences {
        input "minLevel",
            "number",
            title: "Minimum level",
            description: "Minimum brightness level (%). 0% on the dimmer level is mapped to this.",
            required: true,
            multiple: false,
            defaultValue: 0
        
            if (minLevel < 0) {
                minLevel = 0
            } else if (minLevel > 99) {
                minLevel = 99
            }

        input "maxLevel", "number", title: "Maximum level", description: "Maximum brightness level (%). 100% on the dimmer level is mapped to this.", required: true, multiple: false, defaultValue: 100
        if (maxLevel < minLevel) {
            maxLevel = 100
        } 
        else if (maxLevel > 100) {
            maxLevel = 100
        }
        
        input "autoOn",
            "bool",
            title: "Turn on when level adjusted",
            description: "Switch turns on automatically when dimmer level is adjusted.",
            required: true,
            multiple: false,
            defaultValue: true
        
        input "debugEnable", "bool", title: "Enable debug logging", required: false, defaultValue: false
        input "infoEnable", "bool", title: "Enable info logging", required: false, defaultValue: true
    }
}

/*
-----------------------------------------------------------------------------
Logging output
-----------------------------------------------------------------------------
*/

def logDebug(msg) {
    if (settings.debugEnable) {
        log.debug msg
    }
}

def logInfo(msg) {
    if (settings.infoEnable) {
        log.info msg
    }
}

def logWarn(msg) {
    if (settings.debugEnable) {
        log.warn msg
    }
}

/*
-----------------------------------------------------------------------------
Standard handlers
-----------------------------------------------------------------------------
*/

def installed() {
    return initialized()
}

def configure() {
    return initialized()
}

def initialize() {
    return initialized()
}

def updated() {
    logDebug "updated() ..."
    checkDriverVersion()
    //
    if (isTS0601()) {
        ArrayList<String> cmdsTuya = []
        def cmd = "01"
        def eps = parent?.config()?.numEps
        logDebug "config().numEps = ${eps}"
        if (eps > 1) {
            cmd = this.device.getData().componentName[-2..-1]
            logInfo "### updating settings for child device ${this.device.getData().componentName} ... device #${cmd}"
        }
        else {    // single EP device
            logInfo "### updating settings for device ${device.getDataValue("manufacturer")} ${config()}"
        }
        // minLevel
        Integer value = Math.round(this.minLevel * 10)
        def dpValHex  = zigbee.convertToHexString(value as int, 8) 
        logDebug "updated() minLevel value = ${this.minLevel} (raw=$value)"
        def dpCommand = cmd == "01" ? "03" : cmd == "02" ? "09" : cmd == "03" ? "11" : null
        logDebug "sending minLevel command=${dpCommand} value=${value} ($dpValHex)"
        if (isParent()) cmdsTuya += sendTuyaCommand(dpCommand, DP_TYPE_VALUE, dpValHex)
        else cmdsTuya += parent?.sendTuyaCommand(dpCommand, DP_TYPE_VALUE, dpValHex)
        // maxLevel
        value = Math.round(this.maxLevel * 10)
        dpValHex  = zigbee.convertToHexString(value as int, 8) 
        logDebug "updated() maxLevel  value = ${this.maxLevel} (raw=$value)"
        dpCommand = cmd == "01" ? "05" : cmd == "02" ? "0B" : cmd == "03" ? "13" : null
        logDebug "sending maxLevel command=${dpCommand} value=${value} ($dpValHex)"
        if (isParent()) cmdsTuya += sendTuyaCommand(dpCommand, DP_TYPE_VALUE, dpValHex)
        else cmdsTuya += parent?.sendTuyaCommand(dpCommand, DP_TYPE_VALUE, dpValHex)
        //
        hubitat.device.HubMultiAction allActions = new hubitat.device.HubMultiAction()
        cmdsTuya.each {
            allActions.add(new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE))
        }
        sendHubCommand(allActions)        
    }
    if (isParent()) {
        logDebug "updated parent->child"
        getChildByEndpointId(indexToEndpointId(0)).onParentSettingsChange(settings)
    } else {
        logDebug "updated child->parent"
        parent?.onChildSettingsChange(device.deviceNetworkId, settings)
    }
    return initialized()
}

def initialized() {
    def cmds = []
    if (debug == true && deviceSimulation == true) {
        device.updateDataValue("model", simulatedModel)
        device.updateDataValue("manufacturer", simulatedManufacturer)
        logDebug "device simulation: ${simulatedModel} ${simulatedManufacturer}"
    }
    unschedule() // added 12/10/2022
    logDebug "initialized() device.getData() = ${device.getData()}"
    
    if (isParent()) {
        createChildDevices()   
        if (device.getDataValue("model") == "TS0601") {
            logDebug "spelling tuyaBlackMagic()"
            cmds += tuyaBlackMagic()
        }
        cmds += listenChildDevices()
        //
    }
    else {
        logDebug "skipping initialized() for child device"
    }
    return cmds
}


/*
-----------------------------------------------------------------------------
Setting up child devices
-----------------------------------------------------------------------------
*/

def createChildDevices() {
    def numEps = config().numEps
    
    if (numEps == 1) {
        numEps = 0
    }
    logDebug "about to delete ${numEps} expected child devices, actually found ${getChildDevices().size()}"

    def index = getChildDevices().size()
    if (index == null) {
        logDebug "no child devices to delete!"
    }
    else {
        for (int i=0; i<index; i++) {
            def dni = indexToChildDni(i)
            if (dni != null) {
                logInfo "Deleting child ${i} with dni ${dni}"
                deleteChildDevice(dni)    
            }
            else {
                logDebug "child device ${i} DNI was not found!"
            }
        }
    }
    
    logDebug "about to create ${numEps} child devices"   
    for (int i=0; i<numEps; i++) {
        // create child devices
        def dni = indexToChildDni(i)
        def endpointId = indexToEndpointId(i)
        logInfo "Creating child ${i} with dni ${dni}"
        addChildDevice(
            "Tuya Zigbee dimmer module",
            dni,
            [
                completedSetup: true,
                label: "${device.displayName} (CH${endpointId})",
                isComponent:true,
                componentName: "ch${endpointId}",
                componentLabel: "Channel ${endpointId}"
            ]
        )
    }
}


def listenChildDevices() {
    def cmds = []
    if (isTS0601()) {
        return null
    }
    getChildEndpointIds().each{ endpointId ->
        cmds += [
            //bindings
            "zdo bind 0x${device.deviceNetworkId} 0x${endpointId} 0x01 0x0006 {${device.zigbeeId}} {}", "delay 200",
            "zdo bind 0x${device.deviceNetworkId} 0x${endpointId} 0x01 0x0008 {${device.zigbeeId}} {}", "delay 200",
            //reporting
            "he cr 0x${device.deviceNetworkId} 0x${endpointId} 0x0006 0 0x10 0 0xFFFF {}","delay 200",
            "he cr 0x${device.deviceNetworkId} 0x${endpointId} 0x0008 0 0x20 0 0xFFFF {}", "delay 200",
        ] + cmdRefresh([endpointId])
    }
    return cmds
}

/*
-----------------------------------------------------------------------------
Propagate settings changes both ways between Parent and First Child
-----------------------------------------------------------------------------
*/

def onChildSettingsChange(childDni, childSettings) {
    def i = endpointIdToIndex(childDniToEndpointId(childDni))
    logInfo "updating settings for child device #${i+1} ..."
    if (i==0) {
        device.updateSetting("minLevel",childSettings.minLevel)
        device.updateSetting("maxLevel",childSettings.maxLevel)
        device.updateSetting("autoOn",childSettings.autoOn)
    }
    else {
        logWarn "skipped onChildSettingsChange() for child device #${i+1}"
    }
}

def onParentSettingsChange(parentSettings) {
    logInfo "updating settings for the parent device ..."
    device.updateSetting("minLevel",parentSettings.minLevel)
    device.updateSetting("maxLevel",parentSettings.maxLevel)
    device.updateSetting("autoOn",parentSettings.autoOn)
    /*
    if (isTS0601()) {
        logWarn "Min/Max levels NOT changed on the parent device"
    }
*/
}
 
/*
-----------------------------------------------------------------------------
Command handlers

if child, then ask parent to act on its behalf
if parent, then act on endpoint 1
-----------------------------------------------------------------------------
*/

def refresh() {
    if (isParent()) {
        return cmdRefresh(indexToChildDni(0))
    } else {
        parent?.doActions( parent?.cmdRefresh(device.deviceNetworkId) )
    }
}
        
def on() {
    if (isParent()) {
        return cmdSwitch(indexToChildDni(0), 1)
    } else {
        parent?.doActions( parent?.cmdSwitch(device.deviceNetworkId, 1) )
    }
}

def off() {
    if (isParent()) {
        return cmdSwitch(indexToChildDni(0), 0)
    } else {
        parent?.doActions( parent?.cmdSwitch(device.deviceNetworkId, 0) )
    }
}

def toggle() {
    if (isParent()) {
        return cmdSwitchToggle(indexToChildDni(0))
    } else {
        parent?.doActions( parent?.cmdSwitchToggle(device.deviceNetworkId) )
    }
}

def setLevel(level, duration=0) {
    if (isParent()) {
        def value = levelToValue(level)
        return cmdSetLevel(indexToChildDni(0), value, duration)
    } else {
        def value = levelToValue(level)
        parent?.doActions( parent?.cmdSetLevel(device.deviceNetworkId, value, duration) )
    }
}



/*
-----------------------------------------------------------------------------
Hub Action (cmd) generators
-----------------------------------------------------------------------------
*/


def cmdRefresh(String childDni) {
    def endpointId = childDniToEndpointId(childDni)
    if (isTS0601()) {
        logWarn "cmdRefresh NOT implemented for TS0601!"
        return null
    }
    return [
        "he rattr 0x${device.deviceNetworkId} 0x${endpointId} 0x0006 0 {}",
        "delay 100",
        "he rattr 0x${device.deviceNetworkId} 0x${endpointId} 0x0008 0 {}",
        "delay 100"
    ]
}
    
def cmdSwitchToggle(String childDni) {
    def endpointId = childDniToEndpointId(childDni)
    if (isTS0601()) {
        if (device.currentState('switch', true).value == 'on') {
            return cmdSwitch(childDni, 0)
        }
        else {
            return cmdSwitch(childDni, 1)
        }
    }
    return [
        "he cmd 0x${device.deviceNetworkId} 0x${endpointId} 0x0006 2 {}",
        "delay 500"
    ] + cmdRefresh(childDni)
}

def cmdSwitch(String childDni, onOff) {
    logDebug "cmdSwitch: childDni=${childDni} onOff=${onOff}"
    def endpointId = childDniToEndpointId(childDni)
    onOff = onOff ? "1" : "0"
    
    if (isTS0601()) {
        def dpValHex  = zigbee.convertToHexString(onOff as int, 2) 
        def cmd = childDni[-2..-1]
        def dpCommand = cmd == "01" ? "01" : cmd == "02" ? "07" : cmd == "03" ? "0F" : null
        logDebug "${device.displayName}  sending cmdSwitch command=${dpCommand} value=${onOff} ($dpValHex)"
        
        return sendTuyaCommand(dpCommand, DP_TYPE_BOOL, dpValHex)       
    }
    return [
        "he cmd 0x${device.deviceNetworkId} 0x${endpointId} 0x0006 ${onOff} {}"
    ]
}

def cmdSetLevel(String childDni, value, duration) {
    def endpointId = childDniToEndpointId(childDni)
    value = value.toInteger()
    value = value > 255 ? 255 : value
    value = value < 1 ? 0 : value

    duration = (duration * 10).toInteger()
    def child = getChildByEndpointId(endpointId)
    logDebug "cmdSetLevel: child=${child} childDni=${childDni} value=${value} duration=${duration}"
    if (isTS0601()) {
        ArrayList<String> cmdsTuya = []
        value = (value*10) as int 
        def dpValHex  = zigbee.convertToHexString(value as int, 8) 
        def cmd = childDni[-2..-1]
        def dpCommand = cmd == "01" ? "02" : cmd == "02" ? "08" : cmd == "03" ? "10" : null
        if (device.getDataValue("manufacturer") == "_TZE200_fvldku9h") {
            dpCommand = "04"
            dpValHex  = zigbee.convertToHexString((value/10) as int, 8) 
        }
        logDebug "${device.displayName}  sending cmdSetLevel command=${dpCommand} value=${value} ($dpValHex)"
        cmdsTuya = sendTuyaCommand(dpCommand, DP_TYPE_VALUE, dpValHex)
        if (child.isAutoOn() && device.currentState('switch', true).value != 'on') {
            logDebug "${device.displayName}  sending cmdSwitch on for switch #${endpointId}"
            cmdsTuya += cmdSwitch(childDni, 1)
        }
        return cmdsTuya
    }
    
    def cmd = [
        "he cmd 0x${device.deviceNetworkId} 0x${endpointId} 0x0008 4 { 0x${intTo8bitUnsignedHex(value)} 0x${intTo16bitUnsignedHex(duration)} }",
    ]
    
    if (child.isAutoOn()) {
        cmd += "he cmd 0x${device.deviceNetworkId} 0x${endpointId} 0x0006 1 {}"
    }
    return cmd
}

/*
-----------------------------------------------------------------------------
Parent only code
-----------------------------------------------------------------------------
*/

def doActions(List<String> cmds) {
    if (isParent()) {
        hubitat.device.HubMultiAction allActions = new hubitat.device.HubMultiAction()
        
        cmds.each { it ->
            if(it.startsWith('delay') == true) {
                allActions.add(new hubitat.device.HubAction(it, hubitat.device.Protocol.DELAY))
            } else {
                allActions.add(new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE))
            }
        }
        logDebug "Sending actions: ${cmds}"
        sendHubCommand(allActions)
    } else {
        throw new Exception("doActions() called incorrectly by child")
    }
}


def parse(String description) {
    checkDriverVersion()
    logDebug "Received raw: ${description}"

    if (isParent()) {
        def descMap = zigbee.parseDescriptionAsMap(description)
        logDebug "Received parsed: ${descMap}"

        if (description.startsWith("catchall")) {
            logDebug "catchall clusterId=${descMap?.clusterId} command=${descMap?.command} data=${descMap?.data}"
            if (descMap?.clusterId == "EF00") {
                return parseTuyaCluster(descMap)
            }
            else {
                logWarn "Ignored non-Tuya cluster catchall clusterId=${descMap?.clusterId} command=${descMap?.command} data=${descMap?.data}"
                return null
            }
        }
        //
        try {
            def value = Integer.parseInt(descMap.value, 16)
        }
        catch (e) {
            logWarn "exception caught while parsing description:  ${description}"
            value = 0
        }
        def child = getChildByEndpointId(descMap.endpoint)
        def isFirst = 0 == endpointIdToIndex(descMap.endpoint)
            
        switch (descMap.clusterInt) {
            case 0x0000: // basic cluster
                parseBasicCluster( descMap )
                break
            case 0x0006: // switch state
                child.onSwitchState(value)
                if (isFirst && child != this) {
                    logDebug "Replicating switchState in parent"
                    onSwitchState(value)
                } else {
                    logDebug "${isFirst} ${this} ${child} ${value}"
                }
                break
            case 0x0008: // switch level state
                child.onSwitchLevel(value)
                if (isFirst && child != this) {
                    logDebug "Replicating switchLevel in parent"
                    onSwitchLevel(value)
                }
                break
            default :
                logWarn "UNPROCESSED endpoint=${descMap?.endpoint} cluster=${descMap?.cluster} command=${descMap?.command} attrInt = ${descMap?.attrInt} value= ${descMap?.value} data=${descMap?.data}"
                break
        }
    } 
    else {
        throw new Exception("parse() called incorrectly by child")
    }
}

/*
-----------------------------------------------------------------------------
Tuya cluster EF00 specific code
-----------------------------------------------------------------------------
*/

def tuyaBlackMagic() {
    return zigbee.readAttribute(0x0000, [0x0004, 0x000, 0x0001, 0x0005, 0x0007, 0xfffe], [:], delay=200)
}

def parseTuyaCluster( descMap ) {
    if (descMap.clusterId != "EF00") {
        return null
    }
    if (descMap.command == "0B") {
        logDebug "Tuya command 0x0B data=${descMap.data}"
        return null
    }
    if (descMap.command == "24") {
        logDebug "Tuya Time Sync request data=${descMap.data}"
        def offset = 0
        try {
            offset = location.getTimeZone().getOffset(new Date().getTime())
        }
        catch(e) {
            logWarn "Cannot resolve current location. please set location in Hubitat location setting. Setting timezone offset to zero"
        }
        def cmds = zigbee.command(0xEF00, 0x24, "0008" +zigbee.convertToHexString((int)(now()/1000),8) +  zigbee.convertToHexString((int)((now()+offset)/1000), 8))
        logDebug "sending time data : ${cmds}"
        cmds.each{ sendHubCommand(new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE)) }
        return null
    }
    def cmd = descMap.data[2]
    def value = getAttributeValue(descMap.data)
    switch (cmd) {
        case "01" : // Switch1
        case "07" : // Switch2
        case "0F" : // Switch3
            handleTuyaClusterSwitchCmd(cmd, value)
            break
        case "02" : // Brightness1 (switch level state)
        case "08" : // Brightness2
        case "10" : // Brightness3
            logDebug "received: Tuya brighntness(level) cmd=${cmd} value=${value}"
            handleTuyaClusterBrightnessCmd(cmd, value/10 as int)
            break        
        case "03" : // Minimum brightness1
        case "09" : // Minimum brightness2
        case "11" : // Minimum brightness3
            def switchNumber = cmd == "03" ? "01" : cmd == "09" ? "02" : cmd == "11" ? "03" : null
            logDebug "received: minimum brightness switch#${switchNumber} is ${value/10 as int} (raw=${value})"
            handleTuyaClusterMinBrightnessCmd(cmd, value/10 as int)
            break
        case "05" : // Maximum brightness1
        case "0B" : // Maximum brightness2
        case "13" : // Maximum brightness3
            def switchNumber = cmd == "05" ? "01" : cmd == "0B" ? "02" : cmd == "13" ? "03" : null
            logDebug "received: maximum brightness switch#${switchNumber} is ${value/10 as int} (raw=${value})"
            handleTuyaClusterMaxBrightnessCmd(cmd, value/10 as int)
            break
        case "06" : // Countdown1
        case "0C" : // Countdown2
        case "14" : // Countdown3
            def switchNumber = cmd == "06" ? "01" : cmd == "0C" ? "02" : cmd == "14" ? "03" : null
            logInfo "Countdown ${switchNumber} is ${value}s"
            break
        case "04" : // (04) level for _TZE200_fvldku9h
            handleTuyaClusterBrightnessCmd(cmd, value as int)
            break
        case "0A" : // (10)
            logDebug "Unknown Tuya dp= ${cmd} fn=${value}"
            break
        case "0E" : // (14)
            logInfo "Power-on Status Setting is ${value}"
            break
        case "12" : // (18)
            logDebug "Unknown Tuya dp= ${cmd} fn=${value}"
            break
        case "15" : // (21)
            logInfo "Light Mode is ${value}"
            break
        case "1A" : // (26)
            logInfo "Switch backlight ${value}"
            break
        case "40" : // (64)
            logDebug "Unknown Tuya dp= ${cmd} fn=${value}"
            break
        default :
            logWarn "UNHANDLED Tuya cmd=${cmd} value=${value}"
            break
    }
}

def parseBasicCluster( descMap ) {
    switch (descMap.attrId) {
        case "0001" :
            logDebug "Tuya check-in ${descMap.attrId} (${descMap.value})"
            break
        case "0004" : // attrInt = 4 value= _TZE200_vm1gyrso data=null
            logDebug "Tuya check-in ${descMap.attrId} (${descMap.value})"
            break
        default :
            logWarn "unprocessed Basic cluster endpoint=${descMap?.endpoint} cluster=${descMap?.cluster} command=${descMap?.command} attrInt = ${descMap?.attrInt} value= ${descMap?.value} data=${descMap?.data}"
            break
    }
    
}

def handleTuyaClusterSwitchCmd(cmd,value) {
    def switchNumber = cmd == "01" ? "01" : cmd == "07" ? "02" : cmd == "0F" ? "03" : null
    logInfo "Switch ${switchNumber} is ${value==0 ? "off" : "on"}"
    if (config().numEps == 1) {
        onSwitchState(value)
    }
    else {
        def child = getChildByEndpointId(switchNumber)
        def isFirst = 0 == endpointIdToIndex(switchNumber)
        child.onSwitchState(value)
        if (isFirst && child != this) {
            logDebug "Replicating switchState in parent"
            onSwitchState(value)
        }
    }
}

def handleTuyaClusterBrightnessCmd(cmd, value) {
    def switchNumber = cmd == "02" ? "01" : cmd == "08" ? "02" : cmd == "10" ? "03" : "01"
    scaledValue = valueToLevel(value)
    logInfo "Brightness ${switchNumber} is ${scaledValue}% (${value})"
    if (config().numEps == 1)  {
        onSwitchLevel(value)
    }
    else {
        def child = getChildByEndpointId(switchNumber)
        def isFirst = 0 == endpointIdToIndex(switchNumber)
        child.onSwitchLevel(value)
        if (isFirst && child != this) {
            logDebug "Replicating switchLevel in parent"
            onSwitchLevel(value)
        }
    }
}

def handleTuyaClusterMinBrightnessCmd(cmd, value) {
    def switchNumber = cmd == "03" ? "01" : cmd == "09" ? "02" : cmd == "11" ? "03" : null
    if (config().numEps == 1) {
        device.updateSetting("minLevel", [value: value , type:"number"])
        logInfo "minLevel brightness parameter was updated to ${value}%"
    }
    else {
        def child = getChildByEndpointId(switchNumber)
        logDebug "cmd=${cmd} switchNumber=${switchNumber} child = ${child}"
        def isFirst = 0 == endpointIdToIndex(switchNumber)
        child.updateSetting("minLevel", [value: value , type:"number"])
        logInfo "minLevel brightness parameter for switch #${switchNumber} was updated to ${value}%"
        if (isFirst && child != this) {
            logDebug "Replicating minBrightness in parent"
            device.updateSetting("minLevel", [value: value , type:"number"])
        }
    }
}

def handleTuyaClusterMaxBrightnessCmd(cmd, value) {
    def switchNumber = cmd == "05" ? "01" : cmd == "0B" ? "02" : cmd == "13" ? "03" : null
    if (config().numEps == 1) {
        device.updateSetting("maxLevel", [value: value , type:"number"])
        logInfo "maxLevel brightness parameter was updated to ${value}%"
    }
    else {
        def child = getChildByEndpointId(switchNumber)
        logDebug "child = ${child}"
        def isFirst = 0 == endpointIdToIndex(switchNumber)
        child.updateSetting("maxLevel", [value: value , type:"number"])
        logInfo "maxLevel brightness parameter for switch #${switchNumber} was updated to ${value}%"
        if (isFirst && child != this) {
            logDebug "Replicating maxBrightness in parent"
            device.updateSetting("maxLevel", [value: value , type:"number"])
        }
    }
}



private int getAttributeValue(ArrayList _data) {
    int retValue = 0
    try {    
        if (_data.size() >= 6) {
            int dataLength = zigbee.convertHexToInt(_data[5]) as Integer
            int power = 1;
            for (i in dataLength..1) {
                retValue = retValue + power * zigbee.convertHexToInt(_data[i+5])
                power = power * 256
            }
        }
    }
    catch ( e ) {
        log.error "Exception caught while parsing Tuya data : ${_data}"
    }
    return retValue
}

private sendTuyaCommand(int dp, int dpType, int fnCmd, int fnCmdLength) {
	//atomicState.waitingForResponseSinceMillis = now()
	//checkForResponse()
    
	def dpHex = zigbee.convertToHexString(dp, 2)
	def dpTypeHex = zigbee.convertToHexString(dpType, 2)
	def fnCmdHex = zigbee.convertToHexString(fnCmd, fnCmdLength)
	logDebug("sendTuyaCommand: dp=0x${dpHex}, dpType=0x${dpTypeHex}, fnCmd=0x${fnCmdHex}, fnCmdLength=${fnCmdLength}")
	def message = (randomPacketId().toString()
				   + dpHex
				   + dpTypeHex
				   + zigbee.convertToHexString((fnCmdLength / 2) as int, 4)
				   + fnCmdHex)
	logTrace("sendTuyaCommand: message=${message}")
	/*parent?.*/ zigbee.command(CLUSTER_TUYA, ZIGBEE_COMMAND_SET_DATA, message)
}

private randomPacketId() {
	return zigbee.convertToHexString(new Random().nextInt(65536), 4)
}

/*
-----------------------------------------------------------------------------
Child only code
-----------------------------------------------------------------------------
*/

def onSwitchState(value) {
    def valueText = value==1 ? "on":"off"
    sendEvent(name:"switch", value: valueText, descriptionText:"${device.displayName} set ${valueText}", unit: null)
}

def onSwitchLevel(value) {
    def level = valueToLevel(value.toInteger())
    logDebug "onSwitchLevel: Value=${value} level=${level} (value=${value})"
    
    sendEvent(name:"level", value: level, descriptionText:"${device.displayName} set ${level}%", unit: "%")
}
    
/*
-----------------------------------------------------------------------------
Parent only helper functions
-----------------------------------------------------------------------------
*/
    
@Field static def childDniPattern = ~/^([0-9A-Za-z]+)-([0-9A-Za-z]+)$/

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// mappings between index, endpointId and childDni
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

def indexToEndpointId(i) {
    return intTo8bitUnsignedHex(i+1)
}

def endpointIdToIndex(i) {
    //log.trace "endpointIdToIndex i=${i}"
    return Integer.parseInt(i,16) - 1
}

def endpointIdToChildDni(endpointId) {
    //log.trace "endpointIdToChildDni ${endpointId} = ${device.deviceNetworkId} - ${endpointId}"
    //def childDni = "${device.deviceNetworkId}-${endpointId}"
    //log.trace "childDni = ${childDni}"
    return "${device.deviceNetworkId}-${endpointId}"
}

def indexToChildDni(i) {
    return endpointIdToChildDni(indexToEndpointId(i))
}

def childDniToEndpointId(childDni) {
    def match = childDni =~ childDniPattern
    if (match) {
        if (match[0][1] == device.deviceNetworkId) {
            return match[0][2]
        }
    }
    return null
}

def childDnisToEndpointIds(List<String> childDnis) {
    return childDnis.collect{cDni -> childDniToEndpointId(cDni)}
}

// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
// retrieving children, or retrieving parent if no children and endpointId
// is first index
// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

def getChildByEndpointId(endpointId) {
    //log.trace "getChildByEndpointId endpointId=${endpointId}"
    if (endpointIdToIndex(endpointId) == 0 && getChildDevices().size() == 0) {
        //log.trace "getChildByEndpointId returning this: ${this}"
        return this
    } else {
        //log.trace "getChildByEndpointId returning getChildDevice: ${getChildDevice(endpointIdToChildDni(endpointId))}"
        return getChildDevice(endpointIdToChildDni(endpointId))
    }
}

def getChildEndpointIds() {
    if (getChildDevices().size() == 0) {
        return indexToEndpointId(0)
    } else {
        return getChildDevices().collect{childDniToEndpointId(it.getDni())}
    }
}

/*
-----------------------------------------------------------------------------
Child only helper functions
-----------------------------------------------------------------------------
*/

def getDni() {
    return device.deviceNetworkId
}

def isAutoOn() {
    return settings.autoOn
}



def levelToValue(BigDecimal level) {
    return levelToValue(level.toInteger())
}

def levelToValue(Integer level) {
    def mult = isTS0601() ? 1.0 : 2.55 
    Integer minValue = Math.round(settings.minLevel * mult)
    Integer maxValue = Math.round(settings.maxLevel * mult)
    def reScaled =  rescale(level, 0, 100, minValue, maxValue)
    logDebug "level=${level} reScaled=${reScaled}"
    return reScaled
}

def valueToLevel(BigDecimal value) {
    return valueToLevel(value.toInteger())
}

def valueToLevel(Integer value) {
    def mult = isTS0601() ? 1.0 : 2.55 
    Integer minValue = Math.round(settings.minLevel * mult)
    Integer maxValue = Math.round(settings.maxLevel * mult)
    if (value < minValue) return 0
    if (value > maxValue) return 100
    def reScaled = rescale(value, minValue, maxValue, 0, 100)
    logDebug "value=${value} reScaled=${reScaled}"
    return reScaled
}


/*
-----------------------------------------------------------------------------
Shared helper functions
-----------------------------------------------------------------------------
*/

def isParent() {
    return getParent() == null
}

def rescale(value, fromLo, fromHi, toLo, toHi) {
    return Math.round((((value-fromLo)*(toHi-toLo))/(fromHi-fromLo)+toLo))
}

def intTo16bitUnsignedHex(value) {
    def hexStr = zigbee.convertToHexString(value.toInteger(),4)
    return new String(hexStr.substring(2, 4) + hexStr.substring(0, 2))
}

def intTo8bitUnsignedHex(value) {
    return zigbee.convertToHexString(value.toInteger(), 2)
}
//////////////////////////////////////////////////////////////////////////////

private getCLUSTER_TUYA()       { 0xEF00 }
private getSETDATA()            { 0x00 }
private getSETTIME()            { 0x24 }

// Tuya Commands
private getTUYA_REQUEST()       { 0x00 }
private getTUYA_REPORTING()     { 0x01 }
private getTUYA_QUERY()         { 0x02 }
private getTUYA_STATUS_SEARCH() { 0x06 }
private getTUYA_TIME_SYNCHRONISATION() { 0x24 }

// tuya DP type
private getDP_TYPE_RAW()        { "01" }    // [ bytes ]
private getDP_TYPE_BOOL()       { "01" }    // [ 0/1 ]
private getDP_TYPE_VALUE()      { "02" }    // [ 4 byte value ]
private getDP_TYPE_STRING()     { "03" }    // [ N byte string ]
private getDP_TYPE_ENUM()       { "04" }    // [ 0-255 ]
private getDP_TYPE_BITMAP()     { "05" }    // [ 1,2,4 bytes ] as bits


private sendTuyaCommand(dp, dp_type, fncmd) {
    ArrayList<String> cmds = []
    cmds += zigbee.command(CLUSTER_TUYA, SETDATA, PACKET_ID + dp + dp_type + zigbee.convertToHexString((int)(fncmd.length()/2), 4) + fncmd )
    logDebug "${device.displayName} sendTuyaCommand = ${cmds}"
    return cmds
}

private getPACKET_ID() {
    return randomPacketId()
}


void sendZigbeeCommands(ArrayList<String> cmd) {
    logDebug "${device.displayName} sendZigbeeCommands(cmd=$cmd)"
    hubitat.device.HubMultiAction allActions = new hubitat.device.HubMultiAction()
    cmd.each {
            allActions.add(new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE))
    }
    sendHubCommand(allActions)
}

def driverVersionAndTimeStamp() {version() + ' ' + timeStamp() + ((debug==true) ? " debug version!" : " ")}

def checkDriverVersion() {
    if (state.driverVersion != null && driverVersionAndTimeStamp() == state.driverVersion) {
        // no driver version change
    }
    else {
        logDebug "${device.displayName} updating the settings from the current driver version ${state.driverVersion} to the new version ${driverVersionAndTimeStamp()}"
        state.driverVersion = driverVersionAndTimeStamp()
    }
}

def zTest( dpCommand, dpValue, dpTypeString ) {
    ArrayList<String> cmds = []
    def dpType   = dpTypeString=="DP_TYPE_VALUE" ? DP_TYPE_VALUE : dpTypeString=="DP_TYPE_BOOL" ? DP_TYPE_BOOL : dpTypeString=="DP_TYPE_ENUM" ? DP_TYPE_ENUM : null
    def dpValHex = dpTypeString=="DP_TYPE_VALUE" ? zigbee.convertToHexString(dpValue as int, 8) : dpValue

    logWarn "${device.displayName}  sending TEST command=${dpCommand} value=${dpValue} ($dpValHex) type=${dpType}"

    sendZigbeeCommands( sendTuyaCommand(dpCommand, dpType, dpValHex) )
}    



