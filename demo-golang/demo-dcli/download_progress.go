package main

// 下载进度模型
type DownloadProgressWriter struct {
	Total      int
	Downloaded int
	OnProgress func(float64)
}

// 下载进度步长为5回调一次OnProgress
const StartStepOrigin = -1

var StartStep = StartStepOrigin

// 下载完毕后，重置StartStep到原始值
func ResetStartStepToOrigin() {
	StartStep = StartStepOrigin
}

// 实现io.Writer接口
func (pw *DownloadProgressWriter) Write(p []byte) (int, error) {
	pw.Downloaded += len(p)
	if pw.Total > 0 && pw.OnProgress != nil {
		ratio := float64(pw.Downloaded) / float64(pw.Total)
		// 控制OnProgress回调速率，防止bubbletea产生太多消息
		if int(ratio*10)-StartStep >= -StartStepOrigin {
			StartStep = int(ratio * 10)
			pw.OnProgress(ratio)
		}
	}
	return len(p), nil
}
