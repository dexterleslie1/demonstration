<template>
    <div class="dx-container">
        <div class="dx-recent-container">
            <div class="dx-title">最近聊天列表</div>
            <div class="dx-content"
                v-loading.fullscreen.lock="recentListLoading"
                element-loading-text="最近聊天列表加载中。。。"
                @scroll="handleRecentListScroll($event)">
                <div v-for="entry in $store.state.message.recentList" :key="entry.id">
                    {{entry.id}}
                    <el-button @click="handleSetRecentSticky(entry)">置顶</el-button>
                    <el-button @click="handleDeleteRecent(entry)">删除</el-button>
                </div>    
            </div>
        </div>
        <div class="dx-message-roaming-container">
            <div class="dx-title">
                消息漫游
                <el-button @click="handleSimReceiveMessage">接收消息</el-button>
                <el-button @click="handleSendMessageFailed">发送消息失败</el-button>
                <el-button @click="handleSendMessageSuccess">发送消息成功</el-button>
                <el-button @click="handleSendFileWithProgress">发送文件消息</el-button>
            </div>
            <div class="dx-content"
                v-loading.fullscreen.lock="messageRoamingLoading"
                element-loading-text="消息漫游加载中。。。"
                @scroll="handleMessageRoamingScroll($event)"
                ref="messageRoamingContainer">
                <div v-for="entry in $store.state.message.messageRoamingList" :key="entry.id">
                    id:{{entry.id}},content:{{entry.content}},state:{{entry.state}}
                    <span v-if="entry.progress">,progress:{{entry.progress}}%</span>
                    <el-button @click="handleDeleteMessage(entry)">删除</el-button>
                </div>    
            </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            // 最近聊天列表是否加载中
            recentListLoading: false,
            // 消息漫游是否加载中
            messageRoamingLoading: false,
            // 消息漫游滚动旧的高度
            oldMessageRoamingScrollHeight: 0,
        }
    },
    created() {
        let __this = this

        __this.$eventbus.$on('onNewMessageEvent', function(payload) {
            __this.$message.success(`接收到${payload.totalCount}新的消息`)
        })

        // 加载最近聊天列表
        __this.recentListLoading = true
        __this.$store.dispatch('message/loadRecentList')
        .then(function(data) {
            __this.messageRoamingLoading = true
            // 加载消息漫游数据
            __this.$store.dispatch('message/loadRoaming')
            .then(function(data) {

            })
            .catch(function(error) {
                __this.$message.error(error)
            })
            .finally(function() {
                __this.messageRoamingLoading = false
            })
        })
        .catch(function(error) {
            __this.$message.error(error)
        })
        .finally(function() {
            __this.recentListLoading = false
        })
    },
    beforeDestroy() {
        this.$eventbus.off('onNewMessageEvent')
    },
    methods: {
        handleRecentListScroll(e) {
            let __this = this
            if(e.srcElement.scrollTop + e.srcElement.offsetHeight > 
            e.srcElement.scrollHeight - 10) {
                if(__this.recentListLoading==true) {
                    return
                }
                __this.recentListLoading = true
                __this.$store.dispatch('message/loadRecentList')
                .then(function(data) {
                    
                })
                .catch(function(error) {
                    __this.$message.error(error)
                })
                .finally(function() {
                    __this.recentListLoading = false
                })
          }
        },
        handleSetRecentSticky(entry) {
            let __this = this
            __this.$store.dispatch('message/setRecentSticky', entry.id)
            .then(function(data) {
                __this.$message.success(data)
            })
            .catch(function(error) {
                __this.$message.error(error)
            })
        },
        handleDeleteRecent(entry) {
            let __this = this
            __this.$store.dispatch('message/deleteRecent', entry.id)
            .then(function(data) {
                __this.$message.success(data)
            })
            .catch(function(error) {
                __this.$message.error(error)
            })
        },
        handleMessageRoamingScroll(e) {
            let __this = this
            if(e.srcElement.scrollTop<=0) {
                if(__this.messageRoamingLoading==true) {
                    return
                }
                __this.messageRoamingLoading = true

                __this.oldMessageRoamingScrollHeight = e.srcElement.scrollHeight

                __this.$store.dispatch('message/loadRoaming')
                .then(function(data) {
                    __this.$refs.messageRoamingContainer.scrollTop = __this.$refs.messageRoamingContainer.scrollHeight - __this.oldMessageRoamingScrollHeight
                })
                .catch(function(error) {
                    __this.$message.error(error)
                })
                .finally(function() {
                    __this.messageRoamingLoading = false
                })
            }
        },
        // 点击模拟接收消息按钮
        handleSimReceiveMessage() {
            let __this = this
            __this.$store.dispatch('message/loadRoamingMore')
            // .then(function(data) {

            // })
            // .catch(function(error) {

            // })
        },
        // 点击发送消息失败按钮
        handleSendMessageFailed() {
            let __this = this
            __this.$store.dispatch('message/sendMessageFailed')
            .then(function(data) {
                __this.$refs.messageRoamingContainer.scrollTop = __this.$refs.messageRoamingContainer.scrollHeight
            })
        },
        // 点击发送消息成功按钮
        handleSendMessageSuccess() {
            let __this = this
            __this.$store.dispatch('message/sendMessageSuccess')
            .then(function(data) {
                __this.$refs.messageRoamingContainer.scrollTop = __this.$refs.messageRoamingContainer.scrollHeight
            })
        },
        // 点击删除消息按钮
        handleDeleteMessage(entry) {
            let __this = this
            __this.$store.dispatch('message/deleteMessage', entry.id)
            .then(function(data) {
                __this.$message.success(data)
            })
            .catch(function(error) {
                __this.$message.error(error)
            })
        },
        // 点击发送文件消息按钮
        handleSendFileWithProgress() {
            let __this = this
            __this.$store.dispatch('message/sendFileMessage')
            .then(function(data) {
                 __this.$refs.messageRoamingContainer.scrollTop = __this.$refs.messageRoamingContainer.scrollHeight
            })
            .catch(function(error) {
                __this.$message.error(error)
            })
        }
    }
}
</script>

<style scoped>
.dx-container {
    display: flex;
    height: 500px;
}

.dx-container .dx-recent-container {
    flex: 3;
    display: flex;
    flex-direction: column;
}

.dx-container .dx-message-roaming-container {
    flex: 5;
    display: flex;
    flex-direction: column;
}

.dx-title {
}
.dx-content {
    flex: 1;
    overflow-y: scroll;
}
</style>