<template>
    <div style="display:flex;align-items:center;justify-content:center;">
        <div class="dx-group-avatar-container">
                <div
                    v-for="(entry, i) in getLayerContainerCount" :key="i" 
                    class="dx-avatar-layer-container">
                    <div
                        v-for="(imageEntry, j) in getImageListSlice(i)" :key="j"
                        class="dx-avatar-box">
                        <el-avatar
                            :src="imageEntry" 
                            shape="square"
                            :size="getAvatarSize">
                        </el-avatar>
                    </div>
                </div>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            imageList: []
        }
    },
    computed: {
        getLayerContainerCount: function() {
            let length = this.imageList.length
            if(length<=0) {
                return 0
            } else if(length>=1 && length<=2) {
                return 1
            } else if(length>=3 && length <=6) {
                return 2
            } else {
                return 3
            }
        },
        getAvatarSize: function() {
            let length = this.imageList.length
            if(length<=0) {
                return 20
            } else if(length>=0 && length<=4) {
                return 20
            } else if(length>=5) {
                return 13
            }
        }
    },
    created() {
        for(let i=0; i<9; i++) {
            this.imageList.push(require('@/assets/logo.png'))
        }
    },
    methods: {
        getImageListSlice(layerContainerIndex) {
            let length = this.imageList.length
            if(length<=0) {
                return []
            } else if(length==1 || length==2) {
                return this.imageList
            } else if(length==3) {
                if(layerContainerIndex==0) {
                    return this.imageList.slice(0, 1)
                } else if(layerContainerIndex==1) {
                    return this.imageList.slice(1, 3)
                }
            } else if(length==4) {
                if(layerContainerIndex==0) {
                    return this.imageList.slice(0, 2)
                } else if(layerContainerIndex==1) {
                    return this.imageList.slice(2, 4)
                }
            } else if(length==5) {
                if(layerContainerIndex==0) {
                    return this.imageList.slice(0, 2)
                } else if(layerContainerIndex==1) {
                    return this.imageList.slice(2, 5)
                }
            } else if(length==6) {
                if(layerContainerIndex==0) {
                    return this.imageList.slice(0, 3)
                } else if(layerContainerIndex==1) {
                    return this.imageList.slice(3, 7)
                }
            } else if(length==7) {
                if(layerContainerIndex==0) {
                    return this.imageList.slice(0, 1)
                } else if(layerContainerIndex==1) {
                    return this.imageList.slice(1, 4)
                } else if(layerContainerIndex==2) {
                    return this.imageList.slice(4, 7)
                }
            } else if(length==8) {
                if(layerContainerIndex==0) {
                    return this.imageList.slice(0, 2)
                } else if(layerContainerIndex==1) {
                    return this.imageList.slice(2, 5)
                } else if(layerContainerIndex==2) {
                    return this.imageList.slice(5, 8)
                }
            } else {
                if(layerContainerIndex==0) {
                    return this.imageList.slice(0, 3)
                } else if(layerContainerIndex==1) {
                    return this.imageList.slice(3, 6)
                } else if(layerContainerIndex==2) {
                    return this.imageList.slice(6, 9)
                }
            }
        }
    }
}
</script>

<style scoped>
.dx-group-avatar-container {
    height:45px;
    width:45px;
    background-color:gray;
    border-radius:5px;
    display:flex;
    flex-direction:column;
    justify-content:center;
}
.dx-avatar-layer-container {
    display:flex;
    align-items:center;
    justify-content:center;
}
.dx-avatar-box {
    display: flex;
    align-items: center;
    justify-content: center;
    margin: 1px;
}
</style>