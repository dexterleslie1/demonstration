import tkinter as tk
from tkinter import ttk
import random

# 模拟数据生成
def generate_sample_data():
    """生成示例数据"""
    data = []
    departments = ['销售部', '技术部', '财务部', '市场部', '人力资源部']
    for i in range(1, 51):
        name = f'员工{i}'
        age = random.randint(22, 55)
        department = random.choice(departments)
        salary = random.randint(5000, 20000)
        data.append((i, name, age, department, salary))
    return data

class TableWithFilter:
    def __init__(self, root):
        self.root = root
        self.root.title("表格数据过滤示例")
        self.root.geometry("800x600")
        
        # 原始数据
        self.data = generate_sample_data()
        
        # 排序状态跟踪
        self.sort_column = None
        self.sort_order = "ascending"  # ascending 或 descending
        
        # 创建过滤框架
        self.create_filter_frame()
        
        # 创建表格
        self.create_table()
        
        # 显示所有数据
        self.refresh_table()
    
    def create_filter_frame(self):
        """创建过滤输入框"""
        filter_frame = ttk.Frame(self.root, padding="10")
        filter_frame.pack(fill=tk.X, expand=False)
        
        ttk.Label(filter_frame, text="过滤条件: ").pack(side=tk.LEFT, padx=(0, 10))
        
        # 过滤输入框
        self.filter_var = tk.StringVar()
        self.filter_entry = ttk.Entry(filter_frame, textvariable=self.filter_var, width=50)
        self.filter_entry.pack(side=tk.LEFT, padx=(0, 10))
        
        # 过滤按钮
        ttk.Button(filter_frame, text="过滤", command=self.apply_filter).pack(side=tk.LEFT, padx=(0, 10))
        
        # 重置按钮
        ttk.Button(filter_frame, text="重置", command=self.reset_filter).pack(side=tk.LEFT)
        
        # 实时过滤（输入时自动过滤）
        self.filter_var.trace_add("write", lambda *args: self.apply_filter())
    
    def create_table(self):
        """创建表格组件"""
        # 创建滚动条
        self.scrollbar_x = ttk.Scrollbar(self.root, orient=tk.HORIZONTAL)
        self.scrollbar_y = ttk.Scrollbar(self.root, orient=tk.VERTICAL)
        
        # 创建表格
        self.tree = ttk.Treeview(
            self.root,
            columns=("ID", "姓名", "年龄", "部门", "薪资"),
            show="headings",
            yscrollcommand=self.scrollbar_y.set,
            xscrollcommand=self.scrollbar_x.set
        )
        
        # 设置滚动条
        self.scrollbar_y.config(command=self.tree.yview)
        self.scrollbar_x.config(command=self.tree.xview)
        
        self.scrollbar_y.pack(side=tk.RIGHT, fill=tk.Y)
        self.scrollbar_x.pack(side=tk.BOTTOM, fill=tk.X)
        self.tree.pack(fill=tk.BOTH, expand=True, padx=10, pady=10)
        
        # 设置列标题和点击事件
        for col in ("ID", "姓名", "年龄", "部门", "薪资"):
            self.tree.heading(col, text=col, command=lambda c=col: self.sort_by_column(c))
            self.tree.column(col, width=100, anchor=tk.CENTER)
        
        # 设置ID列宽度
        self.tree.column("ID", width=50)
        # 设置姓名列宽度
        self.tree.column("姓名", width=120)
        # 设置薪资列宽度并右对齐
        self.tree.column("薪资", width=120, anchor=tk.E)
    
    def refresh_table(self, filtered_data=None):
        """刷新表格数据"""
        # 清空表格
        for item in self.tree.get_children():
            self.tree.delete(item)
        
        # 显示数据
        data_to_show = filtered_data if filtered_data else self.data
        for row in data_to_show:
            self.tree.insert("", tk.END, values=row)
    
    def apply_filter(self):
        """应用过滤条件"""
        filter_text = self.filter_var.get().lower().strip()
        
        if not filter_text:
            self.refresh_table()
            return
        
        # 过滤数据
        filtered_data = []
        for row in self.data:
            # 检查每一列是否包含过滤文本
            match = False
            for value in row:
                if filter_text in str(value).lower():
                    match = True
                    break
            if match:
                filtered_data.append(row)
        
        self.refresh_table(filtered_data)
    
    def reset_filter(self):
        """重置过滤条件"""
        self.filter_var.set("")
        self.refresh_table()
    
    def sort_by_column(self, column):
        """按指定列排序"""
        # 定义列索引映射
        column_index = {
            "ID": 0,
            "姓名": 1,
            "年龄": 2,
            "部门": 3,
            "薪资": 4
        }
        
        # 切换排序方向
        if self.sort_column == column:
            # 如果点击的是当前排序列，切换排序方向
            self.sort_order = "descending" if self.sort_order == "ascending" else "ascending"
        else:
            # 如果点击的是新列，设置为升序
            self.sort_column = column
            self.sort_order = "ascending"
        
        # 获取当前显示的数据（考虑过滤条件）
        current_filter = self.filter_var.get().lower().strip()
        if current_filter:
            # 如果有过滤条件，先获取过滤后的数据
            data_to_sort = []
            for row in self.data:
                match = False
                for value in row:
                    if current_filter in str(value).lower():
                        match = True
                        break
                if match:
                    data_to_sort.append(row)
        else:
            # 如果没有过滤条件，使用所有数据
            data_to_sort = self.data.copy()
        
        # 根据列类型选择排序键和排序方式
        index = column_index[column]
        
        # 定义排序键函数，处理不同数据类型
        def get_sort_key(row):
            value = row[index]
            # 数字列（ID、年龄、薪资）按数值排序
            if column in ["ID", "年龄", "薪资"]:
                return int(value)
            # 文本列按字符串排序
            return str(value)
        
        # 执行排序
        reverse = self.sort_order == "descending"
        sorted_data = sorted(data_to_sort, key=get_sort_key, reverse=reverse)
        
        # 更新表头显示，添加排序方向指示器
        for col in ("ID", "姓名", "年龄", "部门", "薪资"):
            if col == self.sort_column:
                # 添加排序方向指示器
                direction = "↑" if self.sort_order == "ascending" else "↓"
                self.tree.heading(col, text=f"{col} {direction}", command=lambda c=col: self.sort_by_column(c))
            else:
                # 恢复正常表头
                self.tree.heading(col, text=col, command=lambda c=col: self.sort_by_column(c))
        
        # 更新表格显示
        self.refresh_table(sorted_data)

if __name__ == "__main__":
    root = tk.Tk()
    app = TableWithFilter(root)
    root.mainloop()