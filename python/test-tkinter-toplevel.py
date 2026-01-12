import tkinter as tk
from tkinter import ttk

class FloatingToolbarApp:
    def __init__(self, root):
        self.root = root
        self.root.title("主应用窗口")
        self.root.geometry("600x400")
        
        # 工具栏状态跟踪
        self.toolbar = None
        self.toolbar_created = False
        
        # 创建主窗口内容
        self.create_main_content()
        
        # 跟踪主窗口移动，保持工具栏相对位置
        self.root.bind("<Configure>", self.update_toolbar_position)
        
    def create_main_content(self):
        """创建主窗口内容"""
        self.main_frame = ttk.Frame(self.root, padding="20")
        self.main_frame.pack(fill=tk.BOTH, expand=True)
        
        # 添加标题
        self.title_label = ttk.Label(self.main_frame, text="浮动工具栏演示", 
                                   font=("Arial", 16, "bold"))
        self.title_label.pack(pady=20)
        
        # 添加控制按钮
        self.toolbar_btn = ttk.Button(self.main_frame, text="显示工具栏", 
                                     command=self.toggle_toolbar)
        self.toolbar_btn.pack(pady=10)
        
        # 添加状态显示
        self.status_var = tk.StringVar(value="准备就绪")
        self.status_label = ttk.Label(self.main_frame, textvariable=self.status_var, 
                                     font=("Arial", 12))
        self.status_label.pack(pady=10)
        
        # 添加说明文本
        self.info_text = ttk.Label(self.main_frame, 
                                  text="点击按钮显示/隐藏浮动工具栏\n" 
                                  "工具栏会始终保持在窗口上方", 
                                  justify=tk.CENTER)
        self.info_text.pack(pady=20)
        
    def create_floating_toolbar(self):
        """创建浮动工具栏窗口"""
        # 创建TopLevel窗口
        self.toolbar = tk.Toplevel(self.root)
        self.toolbar.title("工具栏")
        
        # 设置为浮动窗口（总是在最前面）
        self.toolbar.attributes("-topmost", True)
        
        # 移除窗口装饰（可选）
        # self.toolbar.overrideredirect(True)
        
        # 设置初始位置（相对于主窗口右上角）
        self.update_toolbar_position()
        
        # 创建工具栏按钮
        self.toolbar_frame = ttk.Frame(self.toolbar, padding="5")
        self.toolbar_frame.pack()
        
        # 设置工具栏创建标记
        self.toolbar_created = True
        
        # 添加工具按钮
        self.btn_new = ttk.Button(self.toolbar_frame, text="新建", 
                                 command=lambda: self.update_status("新建文件"))
        self.btn_new.grid(row=0, column=0, padx=2, pady=2)
        
        self.btn_open = ttk.Button(self.toolbar_frame, text="打开", 
                                  command=lambda: self.update_status("打开文件"))
        self.btn_open.grid(row=0, column=1, padx=2, pady=2)
        
        self.btn_save = ttk.Button(self.toolbar_frame, text="保存", 
                                  command=lambda: self.update_status("保存文件"))
        self.btn_save.grid(row=0, column=2, padx=2, pady=2)
        
        ttk.Separator(self.toolbar_frame, orient=tk.VERTICAL).grid(row=0, column=3, 
                                                                  padx=5, pady=2, sticky="ns")
        
        self.btn_copy = ttk.Button(self.toolbar_frame, text="复制", 
                                  command=lambda: self.update_status("复制内容"))
        self.btn_copy.grid(row=0, column=4, padx=2, pady=2)
        
        self.btn_cut = ttk.Button(self.toolbar_frame, text="剪切", 
                                 command=lambda: self.update_status("剪切内容"))
        self.btn_cut.grid(row=0, column=5, padx=2, pady=2)
        
        self.btn_paste = ttk.Button(self.toolbar_frame, text="粘贴", 
                                  command=lambda: self.update_status("粘贴内容"))
        self.btn_paste.grid(row=0, column=6, padx=2, pady=2)
        
        ttk.Separator(self.toolbar_frame, orient=tk.VERTICAL).grid(row=0, column=7, 
                                                                  padx=5, pady=2, sticky="ns")
        
        self.btn_about = ttk.Button(self.toolbar_frame, text="关于", 
                                   command=lambda: self.update_status("显示关于信息"))
        self.btn_about.grid(row=0, column=8, padx=2, pady=2)
        
    def toggle_toolbar(self):
        """切换工具栏的显示和隐藏"""
        if not self.toolbar_created:
            # 如果工具栏未创建，则创建
            self.create_floating_toolbar()
            self.toolbar_btn.config(text="隐藏工具栏")
            self.update_status("工具栏已显示")
            # 设置工具栏关闭事件处理
            self.toolbar.protocol("WM_DELETE_WINDOW", self.hide_toolbar)
        else:
            # 切换显示状态
            if self.toolbar.state() == "withdrawn":
                self.toolbar.deiconify()
                self.toolbar_btn.config(text="隐藏工具栏")
                self.update_status("工具栏已显示")
            else:
                self.hide_toolbar()
    
    def hide_toolbar(self):
        """隐藏工具栏"""
        if self.toolbar_created:
            self.toolbar.withdraw()
            self.toolbar_btn.config(text="显示工具栏")
            self.update_status("工具栏已隐藏")
    
    def update_toolbar_position(self, event=None):
        """更新工具栏位置，使其位于主窗口右上角"""
        try:
            # 只有工具栏存在时才更新位置
            if hasattr(self, 'toolbar') and self.toolbar and self.toolbar_created and self.toolbar.state() != "withdrawn":
                # 获取主窗口位置和大小
                root_x = self.root.winfo_x()
                root_y = self.root.winfo_y()
                root_width = self.root.winfo_width()
                
                # 获取工具栏大小
                toolbar_width = self.toolbar.winfo_reqwidth()
                toolbar_height = self.toolbar.winfo_reqheight()
                
                # 计算新位置（右上角）
                new_x = root_x + root_width - toolbar_width - 10
                new_y = root_y + 10
                
                # 设置新位置
                self.toolbar.geometry(f"+{new_x}+{new_y}")
        except Exception as e:
            pass  # 窗口未完全初始化时忽略错误
    
    def update_status(self, action):
        """更新状态显示"""
        self.status_var.set(f"执行操作: {action}")
        
        # 2秒后恢复默认状态
        self.root.after(2000, lambda: self.status_var.set("准备就绪"))

if __name__ == "__main__":
    root = tk.Tk()
    app = FloatingToolbarApp(root)
    root.mainloop()
