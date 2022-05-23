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
ver 0.2.0 11/12/2021 Matt Hammond - added _TZ3000_7ysdnebc
ver 0.2.1 12/29/2021 kkossev      - added cluster 0003 to the fingerprint, model _TZ3000_7ysdnebc
ver 0.2.2 05/23/2022 kkossev      - moved model and inClusters to modelConfigs, added _TZE200_vm1gyrso 3-Gang Dimmer module

*/

import groovy.transform.Field

@Field static def modelConfigs = [
    "_TYZB01_v8gtiaed": [ numEps: 2, model: "TS110F", inClusters: "0000,0004,0005,0006,0008", joinName: "Tuya Zigbee 2-Gang Dimmer module" ],                // '2 gang smart dimmer switch module with neutral'
    "_TYZB01_qezuin6k": [ numEps: 1, model: "TS110F", inClusters: "0000,0004,0005,0006,0008", joinName: "Tuya Zigbee 1-Gang Dimmer module" ],                // '1 gang smart dimmer switch module with neutral'
    "_TZ3000_ktuoyvt5": [ numEps: 1, model: "TS110F", inClusters: "0000,0004,0005,0006,0008", joinName: "Tuya Zigbee 1-Gang Switch module" ],                // '1 gang smart        switch module without neutral'
    "_TZ3000_92chsky7": [ numEps: 2, model: "TS110F", inClusters: "0000,0004,0005,0006,0008", joinName: "Tuya Zigbee 2-Gang Dimmer module (no-neutral)" ],   // '2 gang smart dimmer switch module without neutral'
    "_TZ3000_7ysdnebc": [ numEps: 2, model: "TS110F", inClusters: "0000,0004,0005,0003,0006,0008", joinName: "Tuya 2CH Zigbee dimmer module" ],
    "_TZE200_vm1gyrso": [ numEps: 3, model: "TS0601", inClusters: "0004,0005,EF00,0000",      joinName: "Tuya Zigbee 3-Gang Dimmer module" ],    
    "_TZE200_whpb9yts": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",      joinName: "Tuya Zigbee 1-Gang Dimmer module" ],                // 'Zigbee smart dimmer'
    "_TZE200_ebwgzdqq": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",      joinName: "Tuya Zigbee 1-Gang Dimmer module" ],    
    "_TZE200_9i9dt8is": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",      joinName: "Tuya Zigbee 1-Gang Dimmer module" ],    
    "_TZE200_dfxkcots": [ numEps: 1, model: "TS0601", inClusters: "0004,0005,EF00,0000",      joinName: "Tuya Zigbee 1-Gang Dimmer module" ],    
    "gq8b1uv":          [ numEps: 1, model: "gq8b1uv", inClusters: "0000,0004,0005,0006,0008",joinName: "Tuya Zigbee 1-Gang Dimmer module" ]                 //  TUYATEC Zigbee smart dimmer
]
    
def config() {
    return modelConfigs[device.getDataValue("manufacturer")]
}

def isTS0601() {
    if (isParent()) {
        //log.trace "model = ${device.getDataValue('model')}"
        return device.getDataValue('model') == "TS0601"
    }
    else {
        //log.trace "model = ${parent?.device.getDataValue('model')}"
        return parent?.device.getDataValue('model') == "TS0601"    
    }
}


metadata {
    definition (
        name: "Tuya Zigbee dimmer module",
        namespace: "matthammonddotorg",
        author: "Matt Hammond",
        description: "Driver for Tuya zigbee dimmer modules",
        documentationLink: "https://github.com/matt-hammond-001/hubitat-code/blob/master/drivers/tuya-zigbee-dimmer-module.README.md"
    ) {

        capability "Configuration"
        capability "Refresh"
        capability "Light"
        capability "Switch"
        capability "SwitchLevel"
        
        command "toggle"
        
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

        
        input "autoOn",
            "bool",
            title: "Turn on when level adjusted",
            description: "Switch turns on automatically when dimmer level is adjusted.",
            required: true,
            multiple: false,
            defaultValue: true
        
        input "debugEnable", "bool", title: "Enable debug logging", required: false
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
    if (settings.debugEnable) {
        log.info msg
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
    if (isParent()) {
        logInfo "updated parent->child"
        getChildByEndpointId(indexToEndpointId(0)).onParentSettingsChange(settings)
    } else {
        logInfo "updated child->parent"
        parent?.onChildSettingsChange(device.deviceNetworkId, settings)
    }
    return initialized()
}

def initialized() {
    def cmds = []
    logDebug device.getData()
    
    if (isParent()) {
        createChildDevices()   
        if (device.getDataValue("model") == "TS0601") {
            log.warn "tuyaBlackMagic()"
            cmds += tuyaBlackMagic()
        }
        cmds += listenChildDevices()
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
    
    while (getChildDevices().size() > numEps) {
        // delete child device
        def i = getChildDevices().size()-1
        def dni = indexToChildDni(i)
        
        logInfo "Deleting child ${i}"
        deleteChildDevice(dni)
    }
           
    while (getChildDevices().size() < numEps) {
        // create child devices
        def i = getChildDevices().size()
        def endpointId = indexToEndpointId(i)
        def dni = indexToChildDni(i)
        logInfo "Creating child ${i} with dni ${dni}"
        
        addChildDevice(
            "Tuya Zigbee dimmer module",
            dni,
            [
                completedSetup: true,
                label: "${device.displayName} (CH${endpointId})",
                isComponent: true,
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
    logInfo "onChildSettingsChange: ${i}"
    if (i==0) {
        device.updateSetting("minLevel",childSettings.minLevel)
        device.updateSetting("autoOn",childSettings.autoOn)
    }
}

def onParentSettingsChange(parentSettings) {
    device.updateSetting("minLevel",parentSettings.minLevel)
    device.updateSetting("autoOn",parentSettings.autoOn)
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
        log.warn "cmdRefresh NOT implemented for TS0601!"
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
        log.warn "cmdSwitchToggle NOT implemented for TS0601!"
        return null
    }
    return [
        "he cmd 0x${device.deviceNetworkId} 0x${endpointId} 0x0006 2 {}",
        "delay 500"
    ] + cmdRefresh(childDni)
}

def cmdSwitch(String childDni, onOff) {
    def endpointId = childDniToEndpointId(childDni)
    onOff = onOff ? "1" : "0"
    
    if (isTS0601()) {
        //ArrayList<String> cmds = []
        //def dpType    = DP_TYPE_BOOL
        def dpValHex  = zigbee.convertToHexString(onOff as int, 2) 
        def cmd = childDni[-2..-1]
        def dpCommand = cmd == "01" ? "01" : cmd == "02" ? "07" : cmd == "03" ? "0F" : null
        //log.warn "${device.displayName}  sending cmdSwitch command=${dpCommand} value=${onOff} ($dpValHex)"
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
        
    if (isTS0601()) {
        log.warn "cmdSetLevel NOT implemented for TS0601!"
        return null
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
    logInfo "Received raw: ${description}"

    if (isParent()) {
        def descMap = zigbee.parseDescriptionAsMap(description)
        logDebug "Received parsed: ${descMap}"

        if (description.startsWith("catchall")) {
            log.trace "catchall clusterId=${descMap?.clusterId} command=${descMap?.command} data=${descMap?.data}"
            if (descMap?.clusterId == "EF00") {
                return parseTuyaCluster(descMap)
            }
            else {
                log.warn "Ignored non-Tuya cluster catchall clusterId=${descMap?.clusterId} command=${descMap?.command} data=${descMap?.data}"
                return null
            }
        }
        //
        try {
            def value = Integer.parseInt(descMap.value, 16)
        }
        catch (e) {
            log.warn "exception caught while parsing description:  ${description}"
            value = 0
        }
        def child = getChildByEndpointId(descMap.endpoint)
        def isFirst = 0 == endpointIdToIndex(descMap.endpoint)
            
        switch (descMap.clusterInt) {
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
                log.warn "UNPROCESSED endpoint=${descMap?.endpoint} cluster=${descMap?.cluster} command=${descMap?.command} attrInt = ${descMap?.attrInt} value= ${descMap?.value} data=${descMap?.data}"
                break
        }
    } else {
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
            handleTuyaClusterBrightnessCmd(cmd, value/10 as int)
            break        
        case "03" : // Minimum brightness1
        case "09" : // Minimum brightness2
        case "11" : // Minimum brightness3
            def switchNumber = cmd == "03" ? "01" : cmd == "09" ? "02" : cmd == "11" ? "03" : null
            log.info "Minimum brightness ${switchNumber} is ${value/10 as int}"
            break
        case "05" : // Maximum brightness1
        case "0B" : // Maximum brightness1
        case "13" : // Maximum brightness1
            def switchNumber = cmd == "05" ? "01" : cmd == "0B" ? "02" : cmd == "13" ? "03" : null
            log.info "Maximum brightness ${switchNumber} is ${value/10 as int}"
            break
        case "06" : // Countdown1
        case "0C" : // Countdown2
        case "14" : // Countdown3
            def switchNumber = cmd == "06" ? "01" : cmd == "0C" ? "02" : cmd == "14" ? "03" : null
            log.info "Countdown ${switchNumber} is ${value}s"
            break
        case "OE" : //14
            log.info "Power-on Status Setting is ${value}"
            break
        case "15" : //21
            log.info "Light Mode is ${value}"
            break
        case "1A" : //26
            log.info "Switch backlight ${value}"
            break
        default :
            log.warn "UNHANDLED Tuya cmd=${cmd} value=${value}"
            break
    }
}

def handleTuyaClusterSwitchCmd(cmd,value) {
    def switchNumber = cmd == "01" ? "01" : cmd == "07" ? "02" : cmd == "0F" ? "03" : null
    log.info "Switch ${switchNumber} is ${value==0 ? "off" : "on"}"
    def child = getChildByEndpointId(switchNumber)
    def isFirst = 0 == endpointIdToIndex(switchNumber)
    child.onSwitchState(value)
    if (isFirst && child != this) {
        logDebug "Replicating switchState in parent"
        onSwitchState(value)
    }
    else {
       // logDebug "${isFirst} ${this} ${child} ${value}"
    }
}

def handleTuyaClusterBrightnessCmd(cmd, value) {
    def switchNumber = cmd == "02" ? "01" : cmd == "08" ? "02" : cmd == "10" ? "03" : null
    log.info "Brightness ${switchNumber} is ${value}%"
    def child = getChildByEndpointId(switchNumber)
    def isFirst = 0 == endpointIdToIndex(switchNumber)
    child.onSwitchLevel(value)
    if (isFirst && child != this) {
        logDebug "Replicating switchLevel in parent"
        onSwitchLevel(value)
    }
    else {
       // logDebug "${isFirst} ${this} ${child} ${value}"
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
        log.error "Exception caught : data = ${_data}"
    }
    return retValue
}

private sendTuyaCommand(int dp, int dpType, int fnCmd, int fnCmdLength) {
	atomicState.waitingForResponseSinceMillis = now()
	checkForResponse()
    
	def dpHex = zigbee.convertToHexString(dp, 2)
	def dpTypeHex = zigbee.convertToHexString(dpType, 2)
	def fnCmdHex = zigbee.convertToHexString(fnCmd, fnCmdLength)
	log.trace("sendTuyaCommand: dp=0x${dpHex}, dpType=0x${dpTypeHex}, fnCmd=0x${fnCmdHex}, fnCmdLength=${fnCmdLength}")
	def message = (randomPacketId().toString()
				   + dpHex
				   + dpTypeHex
				   + zigbee.convertToHexString((fnCmdLength / 2) as int, 4)
				   + fnCmdHex)
	logTrace("sendTuyaCommand: message=${message}")
	zigbee.command(CLUSTER_TUYA, ZIGBEE_COMMAND_SET_DATA, message)
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
    logDebug "onSwitchLevel: value=${value}"
    def level = isTS0601() ?  value : valueToLevel(value.toInteger())
    logDebug "onSwitchLevel: Value=${value} level=${level}"
    
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
    return Integer.parseInt(i,16) - 1
}

def endpointIdToChildDni(endpointId) {
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
    if (endpointIdToIndex(endpointId) == 0 && getChildDevices().size() == 0) {
        return this
    } else {
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
    Integer minValue = Math.round(settings.minLevel*2.55)
    return rescale(level, 0, 100, minValue, 255)
}

def valueToLevel(BigDecimal value) {
    return valueToLevel(value.toInteger())
}

def valueToLevel(Integer value) {
    Integer minValue = Math.round(settings.minLevel*2.55)
    if (value < minValue) {
        return 0
    } else {
        return rescale(value, minValue, 255, 0, 100)
    }
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
    if (settings?.logEnable) log.trace "${device.displayName} sendTuyaCommand = ${cmds}"
    //if (state.txCounter != null) state.txCounter = state.txCounter + 1
    return cmds
}

private getPACKET_ID() {
    /*
    state.packetID = ((state.packetID ?: 0) + 1 ) % 65536
    return zigbee.convertToHexString(state.packetID, 4)
    */
    return randomPacketId()
}


void sendZigbeeCommands(ArrayList<String> cmd) {
    if (settings?.logEnable) {log.trace "${device.displayName} sendZigbeeCommands(cmd=$cmd)"}
    hubitat.device.HubMultiAction allActions = new hubitat.device.HubMultiAction()
    cmd.each {
            allActions.add(new hubitat.device.HubAction(it, hubitat.device.Protocol.ZIGBEE))
            //if (state.txCounter != null) state.txCounter = state.txCounter + 1
    }
    sendHubCommand(allActions)
}



def zTest( dpCommand, dpValue, dpTypeString ) {
    ArrayList<String> cmds = []
    def dpType   = dpTypeString=="DP_TYPE_VALUE" ? DP_TYPE_VALUE : dpTypeString=="DP_TYPE_BOOL" ? DP_TYPE_BOOL : dpTypeString=="DP_TYPE_ENUM" ? DP_TYPE_ENUM : null
    def dpValHex = dpTypeString=="DP_TYPE_VALUE" ? zigbee.convertToHexString(dpValue as int, 8) : dpValue

    log.warn "${device.displayName}  sending TEST command=${dpCommand} value=${dpValue} ($dpValHex) type=${dpType}"

    sendZigbeeCommands( sendTuyaCommand(dpCommand, dpType, dpValHex) )
}    



