import tkinter as tk
from tkinter import ttk
import sqlite3
import random

# 数据库初始化
def init_database():
    """初始化SQLite数据库和表结构"""
    conn = sqlite3.connect('employees.db')
    cursor = conn.cursor()
    
    # 创建员工表
    cursor.execute('''
    CREATE TABLE IF NOT EXISTS employees (
        id INTEGER PRIMARY KEY AUTOINCREMENT,
        name TEXT NOT NULL,
        age INTEGER NOT NULL,
        department TEXT NOT NULL,
        salary INTEGER NOT NULL
    )
    ''')
    
    # 检查是否已有数据
    cursor.execute('SELECT COUNT(*) FROM employees')
    count = cursor.fetchone()[0]
    
    # 如果没有数据，插入示例数据
    if count == 0:
        departments = ['销售部', '技术部', '财务部', '市场部', '人力资源部']
        for i in range(1, 51):
            name = f'员工{i}'
            age = random.randint(22, 55)
            department = random.choice(departments)
            salary = random.randint(5000, 20000)
            cursor.execute(
                'INSERT INTO employees (name, age, department, salary) VALUES (?, ?, ?, ?)',
                (name, age, department, salary)
            )
    
    conn.commit()
    conn.close()

# 获取所有员工数据
def get_all_employees():
    """从数据库获取所有员工数据"""
    conn = sqlite3.connect('employees.db')
    cursor = conn.cursor()
    cursor.execute('SELECT id, name, age, department, salary FROM employees')
    data = cursor.fetchall()
    conn.close()
    return data

# 添加员工
def add_employee(name, age, department, salary):
    """添加新员工到数据库"""
    conn = sqlite3.connect('employees.db')
    cursor = conn.cursor()
    cursor.execute(
        'INSERT INTO employees (name, age, department, salary) VALUES (?, ?, ?, ?)',
        (name, age, department, salary)
    )
    employee_id = cursor.lastrowid
    conn.commit()
    conn.close()
    return employee_id

# 更新员工信息
def update_employee(employee_id, name, age, department, salary):
    """更新员工信息"""
    conn = sqlite3.connect('employees.db')
    cursor = conn.cursor()
    cursor.execute(
        'UPDATE employees SET name=?, age=?, department=?, salary=? WHERE id=?',
        (name, age, department, salary, employee_id)
    )
    conn.commit()
    conn.close()

# 删除员工
def delete_employee(employee_id):
    """删除员工"""
    conn = sqlite3.connect('employees.db')
    cursor = conn.cursor()
    cursor.execute('DELETE FROM employees WHERE id=?', (employee_id,))
    conn.commit()
    conn.close()

class EmployeeManagementSystem:
    def __init__(self, root):
        self.root = root
        self.root.title("员工信息管理系统")
        self.root.geometry("900x600")
        
        # 初始化数据库
        init_database()
        
        # 从数据库加载数据
        self.data = get_all_employees()
        
        # 排序状态跟踪
        self.sort_column = None
        self.sort_order = "ascending"  # ascending 或 descending
        
        # 创建过滤框架
        self.create_filter_frame()
        
        # 创建功能按钮框架
        self.create_button_frame()
        
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
    
    def create_button_frame(self):
        """创建功能按钮框架"""
        button_frame = ttk.Frame(self.root, padding="10")
        button_frame.pack(fill=tk.X, expand=False)
        
        # 添加按钮
        ttk.Button(button_frame, text="添加员工", command=self.add_employee_window).pack(side=tk.LEFT, padx=(0, 10))
        
        # 修改按钮
        ttk.Button(button_frame, text="修改员工", command=self.edit_employee_window).pack(side=tk.LEFT, padx=(0, 10))
        
        # 删除按钮
        ttk.Button(button_frame, text="删除员工", command=self.delete_employee).pack(side=tk.LEFT, padx=(0, 10))
        
        # 刷新按钮
        ttk.Button(button_frame, text="刷新数据", command=self.refresh_data).pack(side=tk.LEFT, padx=(0, 10))
        
        # 状态标签
        self.status_var = tk.StringVar()
        self.status_var.set("就绪")
        self.status_label = ttk.Label(button_frame, textvariable=self.status_var, foreground="green")
        self.status_label.pack(side=tk.RIGHT)
    
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
    
    def add_employee_window(self):
        """打开添加员工窗口"""
        # 创建新窗口
        self.add_window = tk.Toplevel(self.root)
        self.add_window.title("添加员工")
        self.add_window.geometry("400x300")
        
        # 创建表单
        self.create_employee_form(self.add_window, "添加", self.save_new_employee)
    
    def edit_employee_window(self):
        """打开修改员工窗口"""
        # 获取选中的行
        selected_item = self.tree.selection()
        if not selected_item:
            self.status_var.set("请先选择要修改的员工")
            return
        
        # 获取选中行的数据
        row_data = self.tree.item(selected_item[0])['values']
        
        # 创建新窗口
        self.edit_window = tk.Toplevel(self.root)
        self.edit_window.title("修改员工")
        self.edit_window.geometry("400x300")
        
        # 创建表单并填充数据
        self.create_employee_form(self.edit_window, "修改", self.update_employee_data, row_data)
    
    def create_employee_form(self, parent, button_text, button_command, row_data=None):
        """创建员工信息表单"""
        # 创建表单框架
        form_frame = ttk.Frame(parent, padding="20")
        form_frame.pack(fill=tk.BOTH, expand=True)
        
        # 姓名
        ttk.Label(form_frame, text="姓名: ").grid(row=0, column=0, sticky=tk.W, pady=10)
        self.name_var = tk.StringVar()
        if row_data:
            self.name_var.set(row_data[1])
        ttk.Entry(form_frame, textvariable=self.name_var, width=30).grid(row=0, column=1, pady=10)
        
        # 年龄
        ttk.Label(form_frame, text="年龄: ").grid(row=1, column=0, sticky=tk.W, pady=10)
        self.age_var = tk.StringVar()
        if row_data:
            self.age_var.set(row_data[2])
        ttk.Entry(form_frame, textvariable=self.age_var, width=30).grid(row=1, column=1, pady=10)
        
        # 部门
        ttk.Label(form_frame, text="部门: ").grid(row=2, column=0, sticky=tk.W, pady=10)
        self.department_var = tk.StringVar()
        if row_data:
            self.department_var.set(row_data[3])
        ttk.Entry(form_frame, textvariable=self.department_var, width=30).grid(row=2, column=1, pady=10)
        
        # 薪资
        ttk.Label(form_frame, text="薪资: ").grid(row=3, column=0, sticky=tk.W, pady=10)
        self.salary_var = tk.StringVar()
        if row_data:
            self.salary_var.set(row_data[4])
        ttk.Entry(form_frame, textvariable=self.salary_var, width=30).grid(row=3, column=1, pady=10)
        
        # 保存按钮
        ttk.Button(form_frame, text=button_text, command=lambda: button_command(row_data)).grid(row=4, column=0, columnspan=2, pady=20)
    
    def save_new_employee(self, row_data=None):
        """保存新员工信息"""
        try:
            # 获取表单数据
            name = self.name_var.get().strip()
            age = int(self.age_var.get().strip())
            department = self.department_var.get().strip()
            salary = int(self.salary_var.get().strip())
            
            # 验证数据
            if not name or not department:
                self.status_var.set("姓名和部门不能为空")
                return
            
            # 添加到数据库
            add_employee(name, age, department, salary)
            
            # 关闭窗口
            self.add_window.destroy()
            
            # 刷新数据
            self.refresh_data()
            
            # 更新状态
            self.status_var.set("员工添加成功")
        except ValueError:
            self.status_var.set("年龄和薪资必须是数字")
        except Exception as e:
            self.status_var.set(f"添加失败: {str(e)}")
    
    def update_employee_data(self, row_data):
        """更新员工信息"""
        try:
            # 获取表单数据
            employee_id = row_data[0]
            name = self.name_var.get().strip()
            age = int(self.age_var.get().strip())
            department = self.department_var.get().strip()
            salary = int(self.salary_var.get().strip())
            
            # 验证数据
            if not name or not department:
                self.status_var.set("姓名和部门不能为空")
                return
            
            # 更新到数据库
            update_employee(employee_id, name, age, department, salary)
            
            # 关闭窗口
            self.edit_window.destroy()
            
            # 刷新数据
            self.refresh_data()
            
            # 更新状态
            self.status_var.set("员工信息更新成功")
        except ValueError:
            self.status_var.set("年龄和薪资必须是数字")
        except Exception as e:
            self.status_var.set(f"更新失败: {str(e)}")
    
    def delete_employee(self):
        """删除选中的员工"""
        # 获取选中的行
        selected_items = self.tree.selection()
        if not selected_items:
            self.status_var.set("请先选择要删除的员工")
            return
        
        # 确认删除
        confirm = tk.messagebox.askyesno("确认删除", "确定要删除选中的员工吗？")
        if not confirm:
            return
        
        try:
            # 删除选中的员工
            for item in selected_items:
                row_data = self.tree.item(item)['values']
                delete_employee(row_data[0])
            
            # 刷新数据
            self.refresh_data()
            
            # 更新状态
            self.status_var.set(f"成功删除{len(selected_items)}名员工")
        except Exception as e:
            self.status_var.set(f"删除失败: {str(e)}")
    
    def refresh_data(self):
        """从数据库刷新数据"""
        try:
            # 重新加载数据
            self.data = get_all_employees()
            
            # 刷新表格
            self.refresh_table()
            
            # 更新状态
            self.status_var.set("数据刷新成功")
        except Exception as e:
            self.status_var.set(f"刷新失败: {str(e)}")

if __name__ == "__main__":
    # 导入messagebox（在主程序中导入避免循环导入问题）
    from tkinter import messagebox
    
    root = tk.Tk()
    app = EmployeeManagementSystem(root)
    root.mainloop()