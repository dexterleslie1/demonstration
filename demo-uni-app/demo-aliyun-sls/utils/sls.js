/**
 * 阿里云 SLS 业务日志上报封装（直接调用 HTTP 接口）
 * 使用 PutWebTracking 接口：POST /logstores/{logstoreName}/track
 * 文档：https://help.aliyun.com/zh/sls/developer-reference/api-sls-2020-12-30-putwebtracking
 * 小程序端需在后台将域名 https://${project}.${host} 加入 request 合法域名
 */

import slsConfig from './slsConfig.js'

let config = null
let queue = []
let flushTimer = null

function getPlatformSource() {
	// #ifdef H5
	return 'h5'
	// #endif
	// #ifdef MP-WEIXIN
	return 'mp-weixin'
	// #endif
	// #ifdef MP-ALIPAY
	return 'mp-alipay'
	// #endif
	// #ifdef MP-BAIDU
	return 'mp-baidu'
	// #endif
	// #ifdef MP-TOUTIAO
	return 'mp-toutiao'
	// #endif
	return 'unknown'
}

/**
 * 初始化 SLS（App 启动时调用一次）
 * @param {Object} [option] - 覆盖 slsConfig 的配置项
 */
export function initSls(option = {}) {
	const merged = { ...slsConfig, ...option }
	if (!merged.enabled || !merged.project || !merged.logstore || merged.project === 'your-project' || merged.logstore === 'your-logstore') {
		console.log('[SLS] 未配置 project/logstore 或已关闭，日志仅输出到 console')
		return
	}
	config = {
		host: merged.host,
		project: merged.project,
		logstore: merged.logstore,
		time: merged.time || 10,
		count: merged.count || 10,
		topic: merged.topic || 'uni-app-business',
		source: merged.source || getPlatformSource()
	}
	console.log('[SLS] 已初始化，使用 HTTP 接口上报')
}

function getTrackUrl() {
	if (!config) return ''
	return `https://${config.project}.${config.host}/logstores/${config.logstore}/track`
}

/** 将单条日志的所有值转为字符串（SLS 要求 __logs__ 内 value 均为 string） */
function normalizeLogItem(item) {
	const out = {}
	for (const k in item) {
		if (Object.prototype.hasOwnProperty.call(item, k)) {
			const v = item[k]
			out[k] = v === null || v === undefined ? '' : String(v)
		}
	}
	return out
}

/**
 * 通过 HTTP 上报多条日志到 SLS
 * @param {Array<Object>} logs - 日志对象数组
 */
function sendToSls(logs) {
	if (!config || !logs || logs.length === 0) return
	const url = getTrackUrl()
	const body = {
		__topic__: config.topic,
		__source__: config.source,
		__logs__: logs.map(normalizeLogItem)
	}
	const bodyStr = JSON.stringify(body)
	const bodyRawSize = getUtf8ByteLength(bodyStr)

	uni.request({
		url,
		method: 'POST',
		header: {
			'Content-Type': 'application/json',
			'x-log-bodyrawsize': String(bodyRawSize),
			'x-log-apiversion': '0.6.0'
		},
		data: bodyStr,
		success(res) {
			if (res.statusCode === 200) {
				// 成功
				return
			}
			const data = res.data || {}
			const msg = data.errorMessage || data.errorCode || ''
			const needWebTracking = msg.indexOf('web tracking') !== -1 || data.errorCode === 'Unauthorized'
			console.warn('[SLS] 上报失败', res.statusCode, res.data)
			if (needWebTracking) {
				uni.showToast({
					title: '请在控制台为该 Logstore 开启 WebTracking',
					icon: 'none',
					duration: 3000
				})
			}
		},
		fail(err) {
			console.warn('[SLS] 请求失败', err)
		}
	})
}

function getUtf8ByteLength(str) {
	let len = 0
	for (let i = 0; i < str.length; i++) {
		const c = str.charCodeAt(i)
		if (c < 0x80) len += 1
		else if (c < 0x800) len += 2
		else if (c < 0xd800 || c >= 0xe000) len += 3
		else {
			i++
			len += 4
		}
	}
	return len
}

function flush() {
	if (queue.length === 0) return
	const toSend = queue.splice(0, config.count)
	sendToSls(toSend)
}

function scheduleFlush() {
	if (flushTimer) return
	flushTimer = setTimeout(() => {
		flushTimer = null
		flush()
	}, config.time * 1000)
}

/**
 * 上报业务日志（按 time/count 批量发送）
 * @param {string} tag - 日志标签
 * @param {string} message - 日志内容
 * @param {Object} [extra] - 额外键值
 */
export function reportBusinessLog(tag, message, extra = {}) {
	const log = {
		tag: String(tag),
		message: String(message),
		timestamp: Date.now(),
		...extra
	}
	if (!config) {
		console.log('[SLS]', log)
		return
	}
	queue.push(log)
	if (queue.length >= config.count) {
		flush()
	} else {
		scheduleFlush()
	}
}

/**
 * 立即上报一条业务日志
 * @param {string} tag - 日志标签
 * @param {string} message - 日志内容
 * @param {Object} [extra] - 额外键值
 */
export function reportBusinessLogImmediate(tag, message, extra = {}) {
	const log = {
		tag: String(tag),
		message: String(message),
		timestamp: Date.now(),
		...extra
	}
	if (!config) {
		console.log('[SLS]', log)
		return
	}
	sendToSls([log])
}

/**
 * 批量上报多条日志
 * @param {Array<Object>} logs - 日志对象数组
 */
export function reportBatchLogs(logs) {
	if (!Array.isArray(logs) || logs.length === 0) return
	const list = logs.map(item => ({
		timestamp: Date.now(),
		...item
	}))
	if (!config) {
		console.log('[SLS] batch', list)
		return
	}
	sendToSls(list)
}
