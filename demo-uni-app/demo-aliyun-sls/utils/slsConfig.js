/**
 * 阿里云 SLS 日志服务配置
 *
 * 本示例使用 WebTracking 接口（匿名写入），不需要 AccessKey ID / Secret。
 * 开启 WebTracking 后，前端直连 SLS 上报，无需鉴权，但该 Logstore 会开放匿名写入，
 * 存在被恶意写脏数据的风险，适合测试或对安全要求不高的场景。
 * 生产环境建议：由后端用 PutLogs + AccessKey 鉴权接收前端日志再写入 SLS，或使用 STS 临时凭证直传。
 *
 * 必须开启 WebTracking 才能用当前 HTTP 接口上报：
 * 1. 登录 https://sls.console.aliyun.com
 * 2. 进入对应 Project -> 日志库(Logstore) -> 点击目标 Logstore 右侧「修改」
 * 3. 在 Logstore 属性中打开「WebTracking」开关并保存
 * 文档：https://help.aliyun.com/zh/sls/use-the-web-tracking-feature-to-collect-logs
 */
export default {
	// 地域服务入口，如 cn-hangzhou.log.aliyuncs.com
	host: 'cn-hangzhou.log.aliyuncs.com',
	// Project 名称
	project: '',
	// Logstore 名称
	logstore: '',
	// 发送日志的时间间隔（秒），默认 10
	time: 10,
	// 单次发送日志条数，默认 10
	count: 10,
	// 自定义主题，便于在控制台区分
	topic: 'uni-app-business',
	// 日志来源，如 h5 / mp-weixin / app
	source: '',
	// 是否启用（未配置 project/logstore 时可设为 false 关闭上报）
	enabled: true
}
