import logo from './logo.svg';
import './App.css';
import React, { useState } from 'react';
import { Button, message, notification, Modal, Spin } from 'antd';

function App() {

  const [loading, setLoading] = useState(false);

  return (
    <div className="App" style={{ padding: 20 }}>
      <Button type="primary" onClick={(e) => {
        message.success("成功")
      }}>消息提示success</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        message.error("错误")
      }}>消息提示error</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        message.warning("警告")
      }}>消息提示warning</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        // 2.5秒后自动关闭
        // 没有遮罩层
        message.loading('加载中...', 2.5)
          .then(() => message.success('加载完成'));
      }}>消息提示 - 2.5秒后自动关闭loading...</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        const closeLoading = message.loading('加载中...', 0/* 表示不自动关闭 loading... */);

        // 2 秒后自动关闭 loading...
        window.setTimeout(() => {
          closeLoading()
        }, 2000)
      }}>消息提示 - 主动关闭loading...</Button>
      <br /><br />

      <Button type="primary" onClick={(e) => {
        notification.open({
          message: '通知标题',
          description: '这是一条详细的通知描述...',
          onClick: () => {
            console.log('通知被点击了！');
          },
        });
      }}>通知提醒open</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        notification.success({ message: '成功', description: '数据已保存' });
      }}>通知提醒success</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        notification.error({ message: '错误', description: '请求失败' });
      }}>通知提醒error</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        notification.info({ message: '信息', description: '新消息来了' });
      }}>通知提醒info</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        notification.warning({ message: '警告', description: '磁盘空间不足' });
      }}>通知提醒warning</Button>
      <br /><br />

      <Button type="primary" onClick={(e) => {
        Modal.info({
          title: '这是标题',
          content: '这是内容...',
          onOk() {
            console.log('用户点击了确定');
          },
        });
      }}>对话框info</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        Modal.success({ title: '成功', content: '操作已完成！' });
      }}>对话框success</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        Modal.error({ title: '错误', content: '提交失败' });
      }}>对话框error</Button>
      &nbsp;<Button type="primary" onClick={(e) => {
        Modal.confirm({
          title: '确认删除？',
          content: '确定要删除这条数据吗？',
          okText: '删除',
          cancelText: '取消',
          onOk() {
            console.log('执行删除操作');
          },
        });
      }}>对话框confirm</Button>
      <br /><br />

      <Button type="primary" onClick={(e) => {
        setLoading(true)

        window.setTimeout(() => {
          setLoading(false)
        }, 2000)
      }}>显示加载中对话框</Button>
      {
        loading && (<div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 9999,
          }}
        >
          <Spin tip="加载中。。。"></Spin>
        </div>)
      }
    </div>
  );
}

export default App;
