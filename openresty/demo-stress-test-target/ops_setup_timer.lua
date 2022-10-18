local cjson = require "cjson"
local restyLock = require "resty.lock"

local _M = { _VERSION = '1.0.0' }

function _M.setup_timer(yydOpsApiDevHost, yydOpsApiDevPort, detectionNodeIdentifiers)
	local cronb_handler = function (premature)
		local dictTemporaryStore = ngx.shared.dict_temporary_store
		local lock, err = restyLock:new("dict_lock",{exptime=60,timeout=2,step=0.1})
                if not lock then
                	ngx.log(ngx.ERR, "创建锁失败: " ..  err)
			return
                end
		local elapsed, err = lock:lock('myConcurrentLock')
                if not elapsed then
                	ngx.log(ngx.ERR, "加锁失败: " .. err)
			return
                end
		if dictTemporaryStore:get("timerConcurrentLock") == nil then
			dictTemporaryStore:set("timerConcurrentLock", "1", 5)
			_M.retrieveDetectionNodeSettings(yydOpsApiDevHost, yydOpsApiDevPort, detectionNodeIdentifiers)
			_M.reportBannedIp(yydOpsApiDevHost, yydOpsApiDevPort)
		end

		local ok, err = lock:unlock()
                if not ok then
                	ngx.log(ngx.ERR, "释放锁失败: " .. err)
                end
	end

	local ok, err = ngx.timer.every(1, cronb_handler)
	if not ok then
		ngx.log(ngx.ERR, "openresty worker pid=" .. ngx.worker.pid() .. " 创建定时器失败，原因：" .. err)
		return
	end
	ngx.log(ngx.ERR, "openresty worker pid=" .. ngx.worker.pid() .. " 创建定时器成功")
end

function _M.reportBannedIp(yydOpsApiDevHost, yydOpsApiDevPort)
	local varDictBanned = ngx.shared.dict_banned
	local varKeys = varDictBanned:get_keys()
	local varLen = #varKeys
	if varLen>0 then
		local detectionNodeIdentifierToReportVOMap = {}
		for i,data in ipairs(varKeys) do
			local varJSON = varDictBanned:get(data)
			local datum = cjson.decode(varJSON)
			local reason = datum["reason"]
			local extra = datum["extra"]
			local detectionNodeIdentifier = datum["detectionNodeIdentifier"]

			if detectionNodeIdentifierToReportVOMap[detectionNodeIdentifier] == nil then
				detectionNodeIdentifierToReportVOMap[detectionNodeIdentifier] = {
					identifier = detectionNodeIdentifier,
					entryList = {}
				}
			end

			local entry = {}
			entry["ip"] = data
			entry["reason"] = reason
			entry["extra"] = extra
			local entryList = detectionNodeIdentifierToReportVOMap[detectionNodeIdentifier]["entryList"]
			entryList[#entryList+1] = entry
		end
		
		for i,data in pairs(detectionNodeIdentifierToReportVOMap) do
			local reportVO = data
			local httpc = require("resty.http").new()
			local res, err = httpc:request_uri("http://" .. yydOpsApiDevHost .. ":" .. yydOpsApiDevPort .. "/api/v1/ops/ipbanned/report", {
				method = "POST",
				body = cjson.encode(reportVO),
				headers = {
					["Content-Type"] = "application/json;charset=utf-8",
				}
			})
			if not res then
				ngx.log(ngx.ERR, "请求接口ipbanned/report失败，原因： ", err)
				return
			end
			if not (res.status == 200) then
				ngx.log(ngx.ERR, "请求接口ipbanned/report失败，服务器响应：", res.body)
				return
			end
			
			ngx.log(ngx.ERR, "成功上报攻击ip，reportVO：" .. cjson.encode(reportVO))
		end

		for i,data in ipairs(varKeys) do
			varDictBanned:delete(data)
		end
	end	
end

function _M.retrieveDetectionNodeSettings(yydOpsApiDevHost, yydOpsApiDevPort, detectionNodeIdentifiers)
	-- 调用远程接口获取ddos防御 总开关状态和geo开关状态
	local httpc = require("resty.http").new()
	
	local res, err = httpc:request_uri("http://" .. yydOpsApiDevHost .. ":" .. yydOpsApiDevPort .. "/api/v1/ops/detectionnode/listByIdentifier?identifiers=" .. detectionNodeIdentifiers, {
		method = "GET",
		headers = {
			["Content-Type"] = "application/x-www-form-urlencoded",
		}
	})
	if not res then
		ngx.log(ngx.ERR, "请求接口listByIdentifier失败，原因： ", err)
		return
	end
	if not (res.status == 200) then
		ngx.log(ngx.ERR, "请求接口listByIdentifier失败，服务器响应：", res.body)
		return
	end
	
	local body   = res.body
	local dataObject = cjson.decode(body)["data"]
	local varDictValve = ngx.shared.dict_valve;
	for identifier in string.gmatch(detectionNodeIdentifiers, "([^,]+)") do
		local detectionNodeObject =  dataObject[identifier]
		local enableAntibot = detectionNodeObject["enableAntibot"]
		local enableGeoGlobal = detectionNodeObject["enableGeoGlobal"]
		local enableGeoChinaMainland = detectionNodeObject["enableGeoChinaMainland"]
		local enableGeoHongkong = detectionNodeObject["enableGeoHongkong"]
		local enableGeoTaiwan = detectionNodeObject["enableGeoTaiwan"]
		local enableGeoBeijing = detectionNodeObject["enableGeoBeijing"]
		local enableGeoFujian = detectionNodeObject["enableGeoFujian"]
		local enableGeoJiangxi = detectionNodeObject["enableGeoJiangxi"]
		local enableGeoHunan = detectionNodeObject["enableGeoHunan"]
		local enableGeoZhejiang = detectionNodeObject["enableGeoZhejiang"]
		local enableGeoChongqing = detectionNodeObject["enableGeoChongqing"]
		local ipWhitelist = detectionNodeObject["ipWhitelist"]
		
		local isLog = false
		local logMessage = "同步远程节点标识：" .. identifier .. " 相关数据到本地"
		
		-- 是否开启antibot
		local valueOriginal = varDictValve:get(identifier .. "#enableAntibot")
		varDictValve:set(identifier .. "#enableAntibot", enableAntibot)
		if not (valueOriginal == enableAntibot) then
			isLog = true
			logMessage = logMessage .. "，enableAntibot=" .. tostring(enableAntibot)
		end

		-- 下面开关，false、不允许访问，true、允许访问
		-- 全球
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoGlobal")
		varDictValve:set(identifier .. "#enableGeoGlobal", enableGeoGlobal)
		if not (valueOriginal == enableGeoGlobal) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoGlobal=" .. tostring(enableGeoGlobal)
                end
				
		-- 中国大陆所有省份
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoChinaMainland")
		varDictValve:set(identifier .. "#enableGeoChinaMainland", enableGeoChinaMainland)
		if not (valueOriginal == enableGeoChinaMainland) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoChinaMainland=" .. tostring(enableGeoChinaMainland)
                end		

		-- 香港
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoHongkong")
		varDictValve:set(identifier .. "#enableGeoHongkong", enableGeoHongkong)
		if not (valueOriginal == enableGeoHongkong) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoHongkong=" .. tostring(enableGeoHongkong)
                end

		-- 台湾
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoTaiwan")
		varDictValve:set(identifier .. "#enableGeoTaiwan", enableGeoTaiwan)
		if not (valueOriginal == enableGeoTaiwan) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoTaiwan=" .. tostring(enableGeoTaiwan)
                end	
	
		-- 北京
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoBeijing")
		varDictValve:set(identifier .. "#enableGeoBeijing", enableGeoBeijing)
		if not (valueOriginal == enableGeoBeijing) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoBeijing=" .. tostring(enableGeoBeijing)
                end

		-- 福建
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoFujian")
		varDictValve:set(identifier .. "#enableGeoFujian", enableGeoFujian)
		if not (valueOriginal == enableGeoFujian) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoFujian=" .. tostring(enableGeoFujian)
                end

		-- 江西
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoJiangxi")
		varDictValve:set(identifier .. "#enableGeoJiangxi", enableGeoJiangxi)
		if not (valueOriginal == enableGeoJiangxi) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoJiangxi=" .. tostring(enableGeoJiangxi)
                end		

		-- 湖南
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoHunan")
		varDictValve:set(identifier .. "#enableGeoHunan", enableGeoHunan)
		if not (valueOriginal == enableGeoHunan) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoHunan=" .. tostring(enableGeoHunan)
                end

		-- 浙江
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoZhejiang")
		varDictValve:set(identifier .. "#enableGeoZhejiang", enableGeoZhejiang)
		if not (valueOriginal == enableGeoZhejiang) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoZhejiang=" .. tostring(enableGeoZhejiang)
                end

		-- 重庆
		local valueOriginal = varDictValve:get(identifier .. "#enableGeoChongqing")
		varDictValve:set(identifier .. "#enableGeoChongqing", enableGeoChongqing)
		if not (valueOriginal == enableGeoChongqing) then
                        isLog = true
                        logMessage = logMessage .. "，enableGeoChongqing=" .. tostring(enableGeoChongqing)
                end

		local valueOriginal = varDictValve:get(identifier .. "#ipWhitelist")
		varDictValve:set(identifier .. "#ipWhitelist", table.concat(ipWhitelist, ",") .. ",")
		if not (valueOriginal == table.concat(ipWhitelist, ",") .. ",") then
                        isLog = true
                        logMessage = logMessage .. "，ipWhitelist=" .. table.concat(ipWhitelist, ",") .. ","
                end
		
		if isLog then
			ngx.log(ngx.ERR, logMessage)
		end
        end
end

return _M

