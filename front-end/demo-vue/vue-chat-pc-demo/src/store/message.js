import Vue from 'vue'

export default {
    namespaced: true,
    state: {
        // 最近聊天列表
        recentList: [],
        // 最近聊天列表当前页码
        recentListCurrentPage: 0,
        // 最近聊天列表是否正在加载中
        recentListLoading: false,
        // 每页最近聊天列表记录数
        recentListPageSize: 50,

        // 最近聊天列表类型
        types: ['ChatFriend', 'ChatGroup'],
        // 演示用的用户数据
        users: [
            {
                id: 1,
                nickname: '用户1'
            },
            {
                id: 2,
                nickname: '用户2'
            },
            {
                id: 3,
                nickname: '用户3'
            },
            {
                id: 4,
                nickname: '用户4'
            },
            {
                id: 5,
                nickname: '用户5'
            }
        ],
        // 演示用的群组数据
        groups: [
            {
                id: 1,
                name: '群组1' /* 0、群聊 1、群发助手 */
            },
            {
                id: 2,
                name: '群组2'
            },
            {
                id: 3,
                name: '群组3'
            },
            {
                id: 4,
                name: '群发助手4'
            },
            {
                id: 5,
                name: '群发助手5'
            }
        ],

        // 消息漫游数据
        messageRoamingList: [],
        // 当前最新消息ID
        latestMessageId: 235,
        // 消息漫游当前页码
        messageRoamingCurrentPage: 0,
        // 消息漫游是否正在加载中
        messageRoamingLoading: false,
        // 每页消息漫游记录数
        messageRoamingPageSize: 50
    },
    getters: {
        /**
         * 随机获取用户
         */
        getRandomUser(state) {
            return function() {
                let length = state.users.length
                let max = length
                let min = 0
                let randomInt = Math.floor(Math.random() * (max - min) ) + min
                return state.users[randomInt]
            }
        },
        /**
         * 获取指定ID用户
         * @param {long} id 
         */
        getUser(state) {
            return function(id) {
                let length = state.users.length
                for(i=0; i<length; i++) {
                    let user = state.users[i]
                    if(user.id==id) {
                        return user
                    }
                }
                return null
            }
        },
        /**
         * 随机获取群组/群发助手
         * @param {any} state 
         * @returns 
         */
        getRandomGroup(state) {
            return function() {
                let length = state.groups.length
                let max = length
                let min = 0
                let randomInt = Math.floor(Math.random() * (max - min) ) + min
                return state.groups[randomInt]
            }
        },
        /**
         * 随机获取最近聊天列表类型
         * @param {any} state 
         * @returns 
         */
        getRandomType(state) {
            return function() {
                let length = state.types.length
                let max = length
                let min = 0
                let randomInt = Math.floor(Math.random() * (max - min) ) + min
                return state.types[randomInt]
            }
        },
        // 获取本地存储消息列表
        getMessageFromCache(state) {
            return function() {
                let key = 'messageCache#'
                let messageCacheJSON = localStorage.getItem(key)
                if(!messageCacheJSON) {
                    messageCacheJSON = '[]'
                }

                let messageCache = JSON.parse(messageCacheJSON)
                return messageCache
            }
        },
        // 获取消息漫游列表合并数据位置
        getMessageRoamingListMergeIndex(state) {
            return function(receiptIdAfter) {
                let length = state.messageRoamingList.length
                let index = -1
                for(let i=0; i<length; i++) {
                    let receiptId = state.messageRoamingList[i].receiptId
                    if(receiptId==receiptIdAfter) {
                        index = i
                        break;
                    }
                }
                return index
            }
        },
    },
    mutations: {
        // 最近聊天列表排序
        sortRecentList(state) {
            if(state.recentList) {
                state.recentList.sort(function(a, b) {
                    if(a.sticky) {
                        return -1
                    }
                })
            }
        },
        // 存储消息到本地存储中
        storeMessageToCache({state}, messageVo) {
            let key = 'messageCache#'
            let messageCacheJSON = localStorage.getItem(key)
            if(!messageCacheJSON) {
                messageCacheJSON = '[]'
            }

            let messageCache = JSON.parse(messageCacheJSON)
            messageCache.push(messageVo)
            messageCacheJSON = JSON.stringify(messageCache)
            localStorage.setItem(key, messageCacheJSON)
        },
        // 删除本地存储消息
        deleteMessageFromCache(state, id) {
            let messageCache = this.getters['message/getMessageFromCache']()
            let length = messageCache.length
            for(let i=0; i<length; i++) {
                let idTemporary = messageCache[i].id
                if(idTemporary==id) {
                    messageCache.splice(i, 1)
                    break;
                }
            }
            
            let key = 'messageCache#'
            let messageCacheJSON = JSON.stringify(messageCache)
            localStorage.setItem(key, messageCacheJSON)
        },
        // 计算本地消息合并位置信息
        mergeMessageRoamingList(state) {
            let messageCache = this.getters['message/getMessageFromCache']()
            let length = messageCache.length
            for(let i=0; i<length; i++) {
                let receiptIdAfter = messageCache[i].receiptIdAfter
                if(!receiptIdAfter) {
                    continue
                }

                let index = this.getters['message/getMessageRoamingListMergeIndex'](receiptIdAfter)
                if(index>=0) {
                    state.messageRoamingList.splice(index+1, 0, messageCache[i])
                }
            }
        },
    },
    actions: {
        /**
         * 加载当前用户最近聊天列表
         * @returns 返回Promise实例
         */
        loadRecentList(context) {
            let promise = new Promise(function(resolve, reject) {
                // 最近聊天列表正在加载中
                if(context.state.recentListLoading==true) {
                    reject('最近聊天列表正在加载中，请稍候。。。')
                    return
                }
                context.state.recentListLoading = true

                // 模拟调用后端接口记载最近聊天列表
                context.state.recentListCurrentPage++
                setTimeout(function() {
                    let start = (context.state.recentListCurrentPage-1)*context.state.recentListPageSize
                    let end = start + context.state.recentListPageSize
                    for(let i=start; i<end; i++) {
                        let type = context.getters.getRandomType()
                        
                        let group
                        let friend
                        let sender
                        if(type=='ChatFriend') {
                            friend = context.getters.getRandomUser()
                        } else if(type=='ChatGroup') {
                            group = context.getters.getRandomGroup()
                            sender = context.getters.getRandomUser()
                        }
                        context.state.recentList.push({
                            badge: 0,
                            group: group,
                            friend: friend,
                            id: i+1,
                            message: {
                                content: `消息内容${i+1}`,
                                sender: sender
                            },
                            mute: true,
                            sticky: false,
                            updateTime: new Date()
                        })
                    }
                    context.commit('sortRecentList')

                    context.state.recentListLoading = false
                    resolve('最近聊天列表加载成功')
                }, 2000)
            })
            return promise
        },
        /**
         * 设置最近聊天列表置顶
         * @param {long} id 
         * @returns 返回Promise实例
         */
        setRecentSticky(context, id) {
            let promise = new Promise(function(resolve, reject) {
                setTimeout(function() {
                    let length = context.state.recentList.length
                    for(let i=0; i<length; i++) {
                        let idTemporary = context.state.recentList[i].id
                        if(id==idTemporary) {
                            context.state.recentList[i].sticky = true
                            break;
                        }
                    }
                    context.commit('sortRecentList')
                    resolve('置顶成功')
                }, 1000)
            })
            return promise
        },
        /**
         * 删除最近聊天列表
         * @param {*} context 
         * @param {*} id 
         */
        deleteRecent(context, id) {
            let promise = new Promise(function(resolve, reject) {
                setTimeout(function() {
                    let length = context.state.recentList.length
                    for(let i=0; i<length; i++) {
                        let idTemporary = context.state.recentList[i].id
                        if(id==idTemporary) {
                            context.state.recentList.splice(i, 1)
                            break;
                        }
                    }
                    resolve('删除成功')
                }, 1000)
            })
            return promise
        },
        
        /**
         * 消息漫游
         * @param {any} context
         * @returns
         */
        loadRoaming(context) {
            let promise = new Promise(function(resolve, reject) {
                // 正在加载中
                if(context.state.messageRoamingLoading==true) {
                    reject('消息漫游正在加载中，请稍候。。。')
                    return
                }
                context.state.messageRoamingLoading = true

                context.state.messageRoamingCurrentPage++
                setTimeout(function() {
                    let start = context.state.latestMessageId - (context.state.messageRoamingCurrentPage-1)*context.state.messageRoamingPageSize
                    let end = start - context.state.messageRoamingPageSize
                    for(let i=start; i>end; i--) {
                        if(i<=0) {
                            break
                        }
                        context.state.messageRoamingList.splice(0, 0, {
                            id: i,
                            userIdFrom: 0,
                            userIdTo: 0,
                            content: `消息${i}`,
                            createTime: new Date(),
                            state: 'Unread',
                            receiptId: i
                        })
                    }

                    context.commit('mergeMessageRoamingList')

                    context.state.messageRoamingLoading = false
                    resolve('消息漫游加载成功')
                }, 2000)
            })
            return promise
        },
        /**
         * 消息漫游加载最新消息
         * @param {any} context 
         */
        loadRoamingMore(context) {
            context.dispatch('loadRoamingMoreInternal')
            .then(function(data) {
                Vue.prototype.$eventbus.$emit('onNewMessageEvent', {totalCount: 5 /* 5条新的消息 */})
            })
            .catch(function(error) {
                
            })
        },
        // 消息漫游加载最新消息
        loadRoamingMoreInternal(context) {
            let promise = new Promise(function(resolve, reject) {
                setTimeout(function() {
                    let startMessageId = context.state.messageRoamingList[context.state.messageRoamingList.length-1].id
                    // 模拟新增5条消息
                    for(let i=0; i<5; i++) {
                        context.state.messageRoamingList.push({
                            id: startMessageId+i+1,
                            userIdFrom: 0,
                            userIdTo: 0,
                            content: `消息${startMessageId+i+1}`,
                            createTime: new Date(),
                            state: 'Unread',
                            receiptId: startMessageId+i+1
                        })
                    }
                    resolve('消息漫游加载最新消息成功')
                }, 1000)
            })
            return promise
        },
        /**
         * 发送消息失败
         * @param {*} context 
         * @returns 
         */
        sendMessageFailed(context) {
            let promise = new Promise(function(resolve, reject) {
                // 获取receiptIdAfter
                let receiptIdAfter = context.state.messageRoamingList[context.state.messageRoamingList.length-1].receiptId
                // 插入消息
                let startMessageId = context.state.messageRoamingList[context.state.messageRoamingList.length-1].id
                let messageId = startMessageId + 1
                let messageVo = {
                    id: messageId,
                    userIdFrom: 0,
                    userIdTo: 0,
                    content: `消息${messageId}`,
                    createTime: new Date(),
                    state: 'Sending',
                    receiptId: messageId,
                    receiptIdAfter: receiptIdAfter
                }
                
                context.state.messageRoamingList.push(messageVo)
                context.commit('storeMessageToCache', messageVo)
                resolve('插入消息到本地缓存成功')

                setTimeout(function() {
                    messageVo.state = 'SentFailed'
                }, 2000)
            })
            return promise
        },
        /**
         * 发送消息成功
         * @param {*} context 
         * @returns 
         */
         sendMessageSuccess(context) {
            let promise = new Promise(function(resolve, reject) {
                // 获取receiptIdAfter
                let receiptIdAfter = context.state.messageRoamingList[context.state.messageRoamingList.length-1].receiptId
                // 插入消息
                let startMessageId = context.state.messageRoamingList[context.state.messageRoamingList.length-1].id
                let messageId = startMessageId + 1
                let messageVo = {
                    id: messageId,
                    userIdFrom: 0,
                    userIdTo: 0,
                    content: `消息${messageId}`,
                    createTime: new Date(),
                    state: 'Sending',
                    receiptId: messageId,
                    receiptIdAfter: receiptIdAfter
                }
                
                context.state.messageRoamingList.push(messageVo)
                context.commit('storeMessageToCache', messageVo)
                resolve('插入消息到本地缓存成功')

                setTimeout(function() {
                    messageVo.state = 'Read'
                    context.commit('deleteMessageFromCache', messageId)
                }, 2000)
            })
            return promise
        },
        /**
         * 发送文件消息，演示上传进度
         * @param {*} context 
         */
        sendFileMessage(context) {
            let promise = new Promise(function(resolve, reject) {
                // 获取receiptIdAfter
                let receiptIdAfter = context.state.messageRoamingList[context.state.messageRoamingList.length-1].receiptId
                // 插入消息
                let startMessageId = context.state.messageRoamingList[context.state.messageRoamingList.length-1].id
                let messageId = startMessageId + 1
                let messageVo = {
                    id: messageId,
                    userIdFrom: 0,
                    userIdTo: 0,
                    content: `文件消息${messageId}`,
                    createTime: new Date(),
                    state: 'Sending',
                    receiptId: messageId,
                    receiptIdAfter: receiptIdAfter,
                    progress: 0
                }
                
                context.state.messageRoamingList.push(messageVo)
                context.commit('storeMessageToCache', messageVo)
                resolve('插入消息到本地缓存成功')

                // 模拟上传进度
                let intervalInstance = setInterval(function() {
                    messageVo.progress = messageVo.progress + 2
                    if(messageVo.progress>=100) {
                        clearInterval(intervalInstance)
                        context.commit('deleteMessageFromCache', messageId)
                        messageVo.state = 'Read'
                    }
                }, 100)
            })
            return promise
        },
        /**
         * 删除消息
         * @param {*}} context 
         * @param {long} id 
         */
        deleteMessage(context, id) {
            let promise = new Promise(function(resolve, reject) {
                setTimeout(function() {
                    let length = context.state.messageRoamingList.length
                    for(let i=0; i<length; i++) {
                        let idTemporary = context.state.messageRoamingList[i].id
                        if(id==idTemporary) {
                            context.state.messageRoamingList.splice(i, 1)
                            context.commit('deleteMessageFromCache', idTemporary)
                            break
                        }
                    }

                    resolve('消息删除成功')
                }, 1000)
            })
            return promise
        }
    }
}