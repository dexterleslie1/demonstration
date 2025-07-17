import React from 'react'
import { useLocation } from 'react-router-dom';

export default function ReactFunctionComponent() {
    // 获取当前路由的 location 对象（包含 pathname）
    const location = useLocation();
    // 当前路径（例如：'/', '/about'）
    const currentPath = location.pathname;

    return (
        <div>ReactFunctionComponent当前路由信息：{currentPath}</div>
    )
}
