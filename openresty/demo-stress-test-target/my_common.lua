local cjson=require 'cjson'
--local geo=require 'resty.maxminddb'

-- 初始化geo
--[[if not geo.initted() then
    geo.init("/usr/local/openresty/GeoLite2-City.mmdb")
end]]

local ip2region = require 'resty.ip2region'
local ip2regionLocation = ip2region.new({
        file = "/usr/local/openresty/ip2region.db",
        dict = "ip_data",
        mode = "memory" -- maybe memory,binary or btree
});

local restyCookie = require "resty.cookie"
local restyUuid = require "resty.uuid"

local _M = { _VERSION = '1.0.0' }

-- 获取客户端真实ip地址
function _M.getClientIp(frontend--[[是否部署为最前端模式]])
	local varHeaders = ngx.req.get_headers()
        local clientIp = varHeaders["x-azure-socketip"]
        if not clientIp then
                clientIp = ngx.var.remote_addr;
        end

        -- 最前端模式防止x-forwarded-for注入
        if frontend then
                ngx.req.clear_header("x-forwarded-for");
                ngx.req.set_header("x-forwarded-for", clientIp);
        end

        return clientIp;
end

-- 获取客户端请求url
function _M.getRequestUrl()
	local fullUrl
        if not (ngx.req.get_headers()["x-front-port"] == nil) then
                fullUrl = ngx.var.scheme .. "://" .. ngx.var.host .. ":" .. ngx.req.get_headers()["x-front-port"] .. ngx.var.request_uri;
        else
                fullUrl = ngx.var.scheme .. "://" .. ngx.var.http_host .. ngx.var.request_uri;
        end
        --ngx.log(ngx.ERR, "+++++++++++++http_host=", ngx.var.http_host, ",host=", ngx.var.host, ",server_port=", ngx.var.server_port, ",port=", ngx.var.port)
        --local cjson = require "cjson"
        --local json = cjson.encode(ngx.req.get_headers())
        --ngx.log(ngx.ERR, "+++++++++++++++++", json)
        return fullUrl;
end

-- 拦截5秒内超过120个请求情况
function _M.ccDetectionReqLimit(clientIp, requestUrl, dictMyLimitReq, dictBanned, dictLogTail)
	local keyLogTail = "logTail"
        if dictLogTail:get(keyLogTail .. clientIp) == nil then
        	-- 默认观察周期5秒
        	local valueDefaultObservationPeriodInSeconds = 5;
        	-- 请求总次数
        	local keySituation1RequestCount = "situation1RequestCount#";
        	-- 允许最大请求总次数
        	local valueSituation1MaximumAllow = 120;

        	-- 设置相关key对应初始化value
        	local requestCount = dictMyLimitReq:get(keySituation1RequestCount .. clientIp);
        	if not requestCount then
                	dictMyLimitReq:set(keySituation1RequestCount .. clientIp, 0, valueDefaultObservationPeriodInSeconds);
        	end

        	-- 判断5秒内是否超过120次
        	local requestCount = dictMyLimitReq:get(keySituation1RequestCount .. clientIp);
        	if requestCount >= valueSituation1MaximumAllow then
                	-- ngx.log(ngx.CRIT, "Client " .. clientIp .. " committed REQ " .. requestCount ..  " times maximum allow " .. valueSituation1MaximumAllow  .. " within " .. valueDefaultObservationPeriodInSeconds .. " seconds, request url=" .. requestUrl);
        
                	-- 重置requestCount防止高频fail2ban日志尾巴问题
                	dictMyLimitReq:set(keySituation1RequestCount .. clientIp, 0);
		
			local reason = "观察周期" .. valueDefaultObservationPeriodInSeconds .. "秒内请求" .. requestCount .. "次，超过允许最大值" .. valueSituation1MaximumAllow .. "次"	
			local varRequestUri = ngx.var.request_uri
			local varHeaders = ""
			if not (ngx.req.get_headers() == nil) then
				varHeaders = cjson.encode(ngx.req.get_headers())
			end
			local varExtra = "请求uri：" .. varRequestUri .. "，请求头：" .. varHeaders
			local datum = {reason = reason, detectionNodeIdentifier = detectionNodeIdentifier, extra = varExtra}
			local varJSON = cjson.encode(datum)
			dictBanned:set(clientIp, varJSON)

                        -- 防止日志尾巴
                        dictLogTail:set(keyLogTail .. clientIp, clientIp, 60);
        	end

        	dictMyLimitReq:incr(keySituation1RequestCount .. clientIp, 1);
	end
end

-- 拦截5秒内超过50个请求情况
function _M.ccDetectionReqAccLimit(clientIp, requestUrl, dictMyLimitReq, dictBanned, dictLogTail)
	local keyLogTail = "logTail"
        if dictLogTail:get(keyLogTail .. clientIp) == nil then
        	-- 默认观察周期5秒
        	local valueDefaultObservationPeriodInSeconds = 5;
        	-- 默认REQAcc committed观察周期60秒
        	local valueDefaultREQAccCommittedObservationPeriodInSeconds = 120;
        	-- 请求总次数
        	local keySituation2RequestCount = "situation2RequestCount#";
        	-- 允许最大请求总次数
        	local valueSituation2RequestCountMaximumAllow = 50;
        	-- 允许最大违规总次数
        	local valueSituation2CommittedCountMaximumAllow = 9;
        	-- 违规总次数
        	local keySituation2CommittedCount = "situation2CommittedCount#";

        	-- 设置相关key对应初始化value
        	local requestCount = dictMyLimitReq:get(keySituation2RequestCount .. clientIp);
        	if not requestCount then
                	dictMyLimitReq:set(keySituation2RequestCount .. clientIp, 0, valueDefaultObservationPeriodInSeconds);
        	end
        	local committedCount = dictMyLimitReq:get(keySituation2CommittedCount .. clientIp);
        	if not committedCount then
                	dictMyLimitReq:set(keySituation2CommittedCount .. clientIp, 0, valueDefaultREQAccCommittedObservationPeriodInSeconds);
        	end

        	-- 判断5秒内是否超过50次
        	local requestCount = dictMyLimitReq:get(keySituation2RequestCount .. clientIp);
        	if requestCount >= valueSituation2RequestCountMaximumAllow then
                	ngx.log(ngx.ERR, "客户端 " .. clientIp .. " REQAcc犯规+1，reqCnt=" .. requestCount .. ";maximumAllow=" .. valueSituation2RequestCountMaximumAllow);

                	dictMyLimitReq:incr(keySituation2CommittedCount .. clientIp, 1);
                	dictMyLimitReq:set(keySituation2RequestCount .. clientIp, 0, valueDefaultObservationPeriodInSeconds);

                	local committedCount = dictMyLimitReq:get(keySituation2CommittedCount .. clientIp);
                	if committedCount >= valueSituation2CommittedCountMaximumAllow then
                		-- ngx.log(ngx.CRIT, "Client " .. clientIp .. " committed REQAcc " .. committedCount ..  " times maximum allow " .. valueSituation2CommittedCountMaximumAllow  .. " within " .. valueDefaultREQAccCommittedObservationPeriodInSeconds .. " seconds, request url=" .. requestUrl);
				dictMyLimitReq:set(keySituation2CommittedCount .. clientIp, 0, valueDefaultREQAccCommittedObservationPeriodInSeconds);
				
                                -- 防止日志尾巴
                                dictLogTail:set(keyLogTail .. clientIp, clientIp, 60);
                	end
        	end

        	dictMyLimitReq:incr(keySituation2RequestCount .. clientIp, 1);
	end
end

function _M.intercept(clientIp)
	-- 判断是否为aws health checker
        local userAgent = ngx.req.get_headers()["user-agent"]
        if not (userAgent == nil) and not (string.find(userAgent, "HealthChecker") == nil and string.find(userAgent, "Edge Health") == nil) then
                --ngx.log(ngx.ERR, "是health checker, user-agent：", userAgent)
                return
        --else
                --ngx.log(ngx.ERR, "不是health checker")
        end

	local varDictBanned = ngx.shared.dict_banned
        local varDictLogTail = ngx.shared.dict_log_tail
	local dictStressStatistic = ngx.shared.dictStressStatistic

	local requestUri = ngx.var.request_uri
        if string.find(requestUri, "favicon") == nil then
                local keyTemp = "count#" .. clientIp
                if dictStressStatistic:get(keyTemp) == nil then
                        dictStressStatistic:set(keyTemp, 1)
                else
                        dictStressStatistic:incr(keyTemp, 1)
                end
        end

	-- 如果违规则拖慢请求
        local keyLogTail = "logTail";
        if not (varDictLogTail:get(keyLogTail .. clientIp) == nil) then
		local randomSeconds = math.random(2, 5)
		ngx.log(ngx.ERR, "客户端 " .. clientIp .. " 触发频率拦截，请求被延迟" .. randomSeconds .. "秒")
                ngx.sleep(randomSeconds)
        end

        local requestUrl = _M.getRequestUrl();
        local dictMyLimitReq = ngx.shared.my_limit_req_store;
        _M.ccDetectionReqLimit(clientIp, requestUrl, dictMyLimitReq, varDictBanned, varDictLogTail);
        _M.ccDetectionReqAccLimit(clientIp, requestUrl, dictMyLimitReq, varDictBanned, varDictLogTail);
end

return _M
