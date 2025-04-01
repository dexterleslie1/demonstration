package main

import (
	"errors"
	"fmt"
	"github.com/charmbracelet/bubbles/progress"
	"github.com/charmbracelet/bubbles/viewport"
	tea "github.com/charmbracelet/bubbletea"
	"github.com/charmbracelet/lipgloss"
	"github.com/manifoldco/promptui"
	"os"
	"strings"
	"sync"
	"time"
)

var logs []string
var globalErr error

func main() {
	defer PrintError()

	// todo ctrl+c处理
	prompt := promptui.Prompt{
		Label:     "是否执行程序",
		IsConfirm: true,
	}
	result, err := prompt.Run()
	if err != nil {
		result = "N"
	} else {
		result = "y"
	}
	install := result == "y"
	if !install {
		return
	}

	for j := 1; j <= 2; j++ {
		if err := SetTitle(fmt.Sprintf("测试标题%d", j)); err != nil {
			//log.Fatalf("expected no err, got %s", err)
			return
		}
		for i := 1; i <= 2; i++ {
			if err := ExecuteCommand(fmt.Sprintf("yum install -y component-%d", i)); err != nil {
				//log.Fatalf("expected no err, got %s", err)
				return
			}
		}

		for i := 1; i <= 2; i++ {
			if err := ExecuteDownload(fmt.Sprintf("https://download.com/file-%d", i), "/tmp/file-xxx"); err != nil {
				//log.Fatalf("expected no err, got %s", err)
				return
			}
		}

		for i := 1; i <= 2; i++ {
			if err := ExecuteFunc(fmt.Sprintf("步骤%d", i), func(p *tea.Program) error {
				time.Sleep(2 * time.Second)
				return nil
			}); err != nil {
				//log.Fatalf("expected no err, got %s", err)
				return
			}
		}
	}
}

func SetTitle(title string) error {
	err := ExecuteCommon("", func(p *tea.Program) error {
		p.Send(SetTitleMsg{Content: title})
		return nil
	}, nil, nil)
	if err != nil {
		return err
	}
	return nil
}

func PrintError() {
	if globalErr != nil {
		s := ""
		promptStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("7")).TabWidth(7)
		errorStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#ff0000"))

		if len(logs) > 0 {
			logStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#00FF00"))
			for i := 0; i < len(logs); i++ {
				s += promptStyle.Render("\t┃ ")
				s += logStyle.Render(logs[i])
				s += "\n"
			}
		}

		if len(s) > 0 {
			s += promptStyle.Render("\t┃ ")
			s += promptStyle.Render("\n")
		}

		s += promptStyle.Render("\t┃ ")
		s += errorStyle.Render(fmt.Sprintf("%s", globalErr))
		fmt.Println(s)
	}
}

//func Println() error {
//	fmt.Println()
//	return nil
//}

func ExecuteCommand(command string) error {
	err := ExecuteCommon("", func(p *tea.Program) error {
		p.Send(SetHintMsg{Content: fmt.Sprintf("正在执行 %s，稍候。。。", command)})
		return nil
	}, func(p *tea.Program) error {
		p.Send(SetHintMsg{Content: fmt.Sprintf("%s", command)})
		return nil
	}, func(p *tea.Program) error {
		// 模拟命令执行过程中输出的日志
		for k := 0; k < 10; k++ {
			if strings.Contains(command, "-2") && k == 5 {
				return errors.New(fmt.Sprintf("模拟执行命令 %s 出错", command))
			}
			p.Send(ProgressiveMsg{Content: fmt.Sprintf("日志%d", k)})
			time.Sleep(200 * time.Millisecond)
		}
		return nil
	})
	if err != nil {
		return err
	}
	return nil
}

func ExecuteDownload(url string, destPath string) error {
	err := ExecuteCommon("", func(p *tea.Program) error {
		p.Send(SetHintMsg{Content: fmt.Sprintf("正在下载 %s 到 %s", url, destPath)})
		p.Send(startDownloadMsg{})
		return nil
	}, func(p *tea.Program) error {
		p.Send(endDownloadMsg{})
		p.Send(SetHintMsg{Content: fmt.Sprintf("完成下载 %s", url)})
		return nil
	}, func(p *tea.Program) error {
		// 模拟下载文件进度
		total := 100
		for i := 0; i <= total; i = i + 10 {
			ratio := float64(i) / float64(total)
			p.Send(downloadProgressMsg(ratio))
			time.Sleep(200 * time.Millisecond)
			//p.Send(ProgressiveMsg{Content: fmt.Sprintf("%d: ratio=%f", time.Now().Unix(), ratio)})
		}

		return nil
	})
	if err != nil {
		return err
	}
	return nil
}

func ExecuteFunc(hint string, execFunc func(p *tea.Program) error) error {
	err := ExecuteCommon(hint, func(p *tea.Program) error {
		p.Send(SetHintMsg{Content: fmt.Sprintf("正在执行 %s，稍候。。。", hint)})
		return nil
	}, func(p *tea.Program) error {
		p.Send(SetHintMsg{Content: hint})
		return nil
	}, execFunc)
	if err != nil {
		return err
	}
	return nil
}

func ExecuteCommon(hint string,
	beforeExecFunc func(p *tea.Program) error,
	afterExecFunc func(p *tea.Program) error,
	execFunc func(p *tea.Program) error) (err error) {
	waitGroup := sync.WaitGroup{}
	waitGroup.Add(1)

	vp := viewport.New(250, 5)
	vp.Style = vp.Style.MarginLeft(7).Foreground(lipgloss.Color("#00FF00"))

	model := &CliModel{
		viewport:         vp,
		hint:             hint,
		downloadProgress: progress.New(progress.WithSolidFill("")),
		interrupted:      true,
	}

	p := tea.NewProgram(model)

	defer func() {
		globalErr = err
		p.Send(SetErrMsg{err: err})

		p.Send(TerminateMsg{})
		waitGroup.Wait()
	}()

	go func() {
		defer waitGroup.Done()

		_, err := p.Run()
		if err != nil {
			fmt.Printf("Alas, there's been an error: %v", err)
			os.Exit(1)
		}
	}()

	if beforeExecFunc != nil {
		err := beforeExecFunc(p)
		if err != nil {
			return err
		}
	}

	if execFunc != nil {
		err := execFunc(p)
		if err != nil {
			return err
		}
	}

	if afterExecFunc != nil {
		err := afterExecFunc(p)
		if err != nil {
			return err
		}
	}

	return nil
}

type CliModel struct {
	title string

	hint string

	progressiveLogs []string
	viewport        viewport.Model

	// 是否显示下载进度条
	displayDownloadProgress bool
	// 文件下载进度条
	downloadProgress progress.Model

	err error

	// 是否被中断执行
	interrupted bool
}

func (m CliModel) Init() tea.Cmd {
	return nil
}

type ProgressiveMsg struct {
	Content string
}
type TerminateMsg struct {
}

// 文件下载进度消息
type downloadProgressMsg float64

// 开始下载消息
type startDownloadMsg struct {
}

// 结束下载消息
type endDownloadMsg struct {
}

type SetHintMsg struct {
	Content string
}

type SetTitleMsg struct {
	Content string
}

type SetErrMsg struct {
	err error
}

func (m CliModel) Update(msg tea.Msg) (tea.Model, tea.Cmd) {
	switch msg := msg.(type) {

	// Is it a key press?
	case tea.KeyMsg:

		// Cool, what was the actual key pressed?
		switch msg.String() {

		// These keys should exit the program.
		case "ctrl+c", "q":
			return m, tea.Quit
		}

	case ProgressiveMsg:
		contentList := strings.Split(msg.Content, "\n")
		if len(contentList) > 0 {
			m.progressiveLogs = append(m.progressiveLogs, contentList...)
		}
		// m.progressiveLogs = append(m.progressiveLogs, msg.Content)
		return m, nil

	case TerminateMsg:
		m.interrupted = false
		logs = append(logs, m.progressiveLogs...)
		m.progressiveLogs = []string{}
		return m, tea.Quit

	case downloadProgressMsg:
		var cmds []tea.Cmd

		if msg >= 1.0 {
			msg = 1
		}

		cmds = append(cmds, m.downloadProgress.SetPercent(float64(msg)))
		return m, tea.Batch(cmds...)

	// FrameMsg is sent when the progress bar wants to animate itself
	case progress.FrameMsg:
		progressModel, cmd := m.downloadProgress.Update(msg)
		m.downloadProgress = progressModel.(progress.Model)
		return m, cmd

	case tea.WindowSizeMsg:
		m.downloadProgress.Width = msg.Width - 15
		// if m.downloadProgress.Width > maxWidth {
		// m.downloadProgress.Width = maxWidth
		// }
		return m, nil
		// 开始下载文件
	case startDownloadMsg:
		// 显示文件下载进度条
		m.displayDownloadProgress = true
		return m, nil
		// 完成下载文件
	case endDownloadMsg:
		// 重置下载进度发送消息步长
		ResetStartStepToOrigin()
		m.downloadProgress.SetPercent(0)
		// 隐藏文件下载进度条
		m.displayDownloadProgress = false
		return m, nil

	case SetHintMsg:
		m.hint = msg.Content
		return m, nil

	case SetTitleMsg:
		m.title = msg.Content
		return m, nil

	case SetErrMsg:
		m.err = msg.err
		return m, nil

	default:
		return m, nil
	}

	// Return the updated model to the Bubble Tea runtime for processing.
	// Note that we're not returning a command.
	return m, nil
}

func (m CliModel) View() string {
	titleStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#04B575")).Bold(true).Underline(true)
	signCorrectStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#00FF00")).TabWidth(4)
	signIncorrectStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#FF0000")).TabWidth(4)
	contentStyle := lipgloss.NewStyle().MarginLeft(1)

	s := ""

	if len(m.title) > 0 {
		s += "\n"
		s += titleStyle.Render(m.title)
	}

	if len(m.hint) > 0 {
		if m.err == nil {
			s += signCorrectStyle.Render("\t✔") + contentStyle.Render(m.hint)
		} else {
			s += signIncorrectStyle.Render("\t✖") + contentStyle.Render(m.hint)
		}
	}

	if len(m.progressiveLogs) > 0 {
		s += "\n"
		viewportContent := ""

		promptStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("7"))

		if len(m.progressiveLogs) > 0 {
			logStyle := lipgloss.NewStyle().Foreground(lipgloss.Color("#00FF00"))
			for i := 0; i < len(m.progressiveLogs); i++ {
				viewportContent += promptStyle.Render("┃ ")
				viewportContent += logStyle.Render(m.progressiveLogs[i])
				viewportContent += "\n"
			}
		}

		if len(viewportContent) > 0 {
			m.viewport.SetContent(viewportContent)
			m.viewport.GotoBottom()
			s += m.viewport.View()
		}
	}

	if m.displayDownloadProgress {
		s += "\n   下载中 " + m.downloadProgress.View() + "\n"
	}

	s += "\n"
	return s
}
