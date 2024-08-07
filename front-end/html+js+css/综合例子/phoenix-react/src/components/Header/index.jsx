import React, { Component } from 'react'
import './index.scss'

export default class Header extends Component {
    render() {
        return (
            <header>
                <div className="left">
                    <div className="logo-image">
                        <img src="data:image/png;base64,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"
                            alt="phoenix"/>
                    </div>
                    <p className="logo-text">phoenix</p>
                </div>
                <div className="center">
                    <div className="search-container">
                        <svg t="1712289943112" className="icon" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="20220">
                            <path
                                d="M419.405333 0c231.683703 0 419.456512 185.265077 419.456512 413.724553a408.862625 408.862625 0 0 1-101.179298 269.55557l275.748132 280.098278a35.108244 35.108244 0 0 1-0.767673 50.154634 36.336521 36.336521 0 0 1-50.819951-0.716495l-275.49224-279.944743a421.19657 421.19657 0 0 1-266.945482 94.628488C187.823987 827.500285 0 642.235208 0 413.724553 0 185.265077 187.823987 0 419.405333 0z m0 70.932983c-191.918243 0-347.499965 153.483416-347.499965 342.79157 0 189.359333 155.581722 342.842749 347.499965 342.842749s347.551143-153.534594 347.551144-342.842749c0-189.359333-155.581722-342.791571-347.499965-342.79157z"
                                p-id="20221"></path>
                        </svg>
                        <div className="search-result">
                            <div className="search-result-count"><span className="count">13</span> Results</div>
                            <div className="search-result-category">
                                <div className="search-result-category-title">Recently Searched</div>
                                <div className="search-result-category-item-container">
                                    <div className="search-result-category-item">
                                        <svg t="1712378608287" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="21246">
                                            <path
                                                d="M571.52 151.616c198.176 0 358.848 160.672 358.848 358.88s-160.64 358.88-358.88 358.88a357.856 357.856 0 0 1-252.768-104.128 173.984 173.984 0 0 1-5.504-5.792 49.664 49.664 0 0 1 3.2-70.176l0.864-0.768 0.896-0.736 16.224 19.52a307.392 307.392 0 0 0 237.12 111.328 308.128 308.128 0 1 0 0-616.256c-155.2 0-283.616 114.752-305.024 264.032l64.32-64.288c19.808 19.84 19.808 51.968 0 71.776l-69.088 69.056a25.376 25.376 0 0 1-35.872 0L156.8 473.888a50.752 50.752 0 0 1-3.296-68.16l3.296-3.616 59.168 59.2c24-174.944 174.016-309.696 355.52-309.696z m-14.528 199.36c14.016 0 25.376 11.392 25.376 25.408v164.928l85.568 64.16c11.2 8.416 13.472 24.32 5.056 35.52l-10.432 13.92a8 8 0 0 1-11.2 1.6l-119.744-89.824v-207.68a8 8 0 0 1 8-8h17.376z"
                                                p-id="21247"></path>
                                        </svg>
                                        <div>Store Mackbook</div>
                                    </div>
                                    <div className="search-result-category-item">
                                        <svg t="1712378608287" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="21246">
                                            <path
                                                d="M571.52 151.616c198.176 0 358.848 160.672 358.848 358.88s-160.64 358.88-358.88 358.88a357.856 357.856 0 0 1-252.768-104.128 173.984 173.984 0 0 1-5.504-5.792 49.664 49.664 0 0 1 3.2-70.176l0.864-0.768 0.896-0.736 16.224 19.52a307.392 307.392 0 0 0 237.12 111.328 308.128 308.128 0 1 0 0-616.256c-155.2 0-283.616 114.752-305.024 264.032l64.32-64.288c19.808 19.84 19.808 51.968 0 71.776l-69.088 69.056a25.376 25.376 0 0 1-35.872 0L156.8 473.888a50.752 50.752 0 0 1-3.296-68.16l3.296-3.616 59.168 59.2c24-174.944 174.016-309.696 355.52-309.696z m-14.528 199.36c14.016 0 25.376 11.392 25.376 25.408v164.928l85.568 64.16c11.2 8.416 13.472 24.32 5.056 35.52l-10.432 13.92a8 8 0 0 1-11.2 1.6l-119.744-89.824v-207.68a8 8 0 0 1 8-8h17.376z"
                                                p-id="21247"></path>
                                        </svg>
                                        <div>MacBook Air - 13"</div>
                                    </div>
                                </div>
                            </div>

                            <div className="search-result-category">
                                <div className="search-result-category-title">Products</div>
                                <div className="search-result-category-item-container">
                                    <div className="search-result-category-item">
                                        <img className="fit-cover rounded-3"
                                            src="data:image/png;base64,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"
                                            alt="MacBook Air - 13″"/>
                                            <div>
                                                <div className="title">MacBook Air - 13"</div>
                                                <div className="desc">8GB Memory - 1.6GHz - 128GB Storage</div>
                                            </div>
                                    </div>
                                    <div className="search-result-category-item">
                                        <img className="fit-cover rounded-3"
                                            src="data:image/png;base64,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"
                                            alt="MacBook Air - 13″"/>
                                            <div>
                                                <div className="title">MacBook Pro - 13"</div>
                                                <div className="desc">30 Sep at 12:30 PM</div>
                                            </div>
                                    </div>
                                </div>
                            </div>

                            <div className="search-result-category">
                                <div className="search-result-category-title">Quick Links</div>
                                <div className="search-result-category-item-container">
                                    <div className="search-result-category-item">
                                        <svg t="1712462924279" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="25031" width="200" height="200">
                                            <path
                                                d="M593.94368 715.648a10.688 10.688 0 0 0-14.976 0L424.21568 870.4c-71.68 71.68-192.576 79.232-271.68 0-79.232-79.232-71.616-200 0-271.616l154.752-154.752a10.688 10.688 0 0 0 0-15.04l-52.992-52.992a10.688 10.688 0 0 0-15.04 0L84.50368 530.688a287.872 287.872 0 0 0 0 407.488 288 288 0 0 0 407.488 0l154.752-154.752a10.688 10.688 0 0 0 0-15.04l-52.736-52.736z m344.384-631.168a288.256 288.256 0 0 1 0 407.616l-154.752 154.752a10.688 10.688 0 0 1-15.04 0l-52.992-52.992a10.688 10.688 0 0 1 0-15.104l154.752-154.688c71.68-71.68 79.232-192.448 0-271.68-79.104-79.232-200-71.68-271.68 0L443.92768 307.2a10.688 10.688 0 0 1-15.04 0l-52.864-52.864a10.688 10.688 0 0 1 0-15.04l154.88-154.752a287.872 287.872 0 0 1 407.424 0z m-296.32 240.896l52.672 52.736a10.688 10.688 0 0 1 0 15.04l-301.504 301.44a10.688 10.688 0 0 1-15.04 0l-52.736-52.672a10.688 10.688 0 0 1 0-15.04l301.632-301.504a10.688 10.688 0 0 1 15.04 0z"
                                                p-id="25032"></path>
                                        </svg>
                                        <div>Support MacBook House</div>
                                    </div>
                                    <div className="search-result-category-item">
                                        <svg t="1712462924279" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="25031" width="200" height="200">
                                            <path
                                                d="M593.94368 715.648a10.688 10.688 0 0 0-14.976 0L424.21568 870.4c-71.68 71.68-192.576 79.232-271.68 0-79.232-79.232-71.616-200 0-271.616l154.752-154.752a10.688 10.688 0 0 0 0-15.04l-52.992-52.992a10.688 10.688 0 0 0-15.04 0L84.50368 530.688a287.872 287.872 0 0 0 0 407.488 288 288 0 0 0 407.488 0l154.752-154.752a10.688 10.688 0 0 0 0-15.04l-52.736-52.736z m344.384-631.168a288.256 288.256 0 0 1 0 407.616l-154.752 154.752a10.688 10.688 0 0 1-15.04 0l-52.992-52.992a10.688 10.688 0 0 1 0-15.104l154.752-154.688c71.68-71.68 79.232-192.448 0-271.68-79.104-79.232-200-71.68-271.68 0L443.92768 307.2a10.688 10.688 0 0 1-15.04 0l-52.864-52.864a10.688 10.688 0 0 1 0-15.04l154.88-154.752a287.872 287.872 0 0 1 407.424 0z m-296.32 240.896l52.672 52.736a10.688 10.688 0 0 1 0 15.04l-301.504 301.44a10.688 10.688 0 0 1-15.04 0l-52.736-52.672a10.688 10.688 0 0 1 0-15.04l301.632-301.504a10.688 10.688 0 0 1 15.04 0z"
                                                p-id="25032"></path>
                                        </svg>
                                        <div>Store MacBook"</div>
                                    </div>
                                </div>
                            </div>

                            <div className="search-result-category">
                                <div className="search-result-category-title">Files</div>
                                <div className="search-result-category-item-container">
                                    <div className="search-result-category-item">
                                        <svg t="1712462827251" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="24058">
                                            <path
                                                d="M935.082667 480a401.194667 401.194667 0 0 0-1.194667-10.666667h-0.170667c0.426667 3.541333 1.109333 7.04 1.365334 10.666667zM931.285333 450.474667l0 0zM933.717333 469.333333h0.170667a502.613333 502.613333 0 0 0-2.56-18.858666c0.853333 6.272 1.578667 12.586667 2.389333 18.858666zM810.666667 297.088c0-11.093333-10.453333-19.669333-16.042667-22.016l-136.832-131.882667C655.402667 137.813333 646.016 128 634.453333 128H597.333333v213.333333h213.333334V297.088z"
                                                p-id="24059"></path>
                                            <path
                                                d="M554.666667 128H261.12C233.685333 128 213.333333 150.826667 213.333333 177.28v670.805333C213.333333 874.538667 233.642667 896 261.12 896h497.792c27.477333 0 51.754667-21.461333 51.754667-47.914667V384h-256V128z m149.546666 640H318.72C307.626667 768 298.666667 757.674667 298.666667 746.666667s8.96-21.333333 20.010666-21.333334h385.536c11.008 0 20.010667 10.325333 20.010667 21.333334s-9.002667 21.333333-20.010667 21.333333z m0-213.333333c11.008 0 20.010667 10.325333 20.010667 21.333333s-9.002667 21.333333-20.010667 21.333333H318.72C307.626667 597.333333 298.666667 587.008 298.666667 576s8.96-21.333333 20.010666-21.333333h385.536z"
                                                p-id="24060"></path>
                                        </svg>
                                        <div>Library MacBook folder.rar</div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <input type="text" placeholder="Search..."/>
                    </div>
                </div>
                <div className="right">
                    <div className="theme-switcher">
                        <div className="prompt">
                            <div className="content">Switch to dark theme</div>
                        </div>

                        <svg t="1712466477823" className="icon" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="26181" width="200" height="200">
                            <path
                                d="M512.001559 773.066053c-69.732521 0-135.292072-27.155389-184.600241-76.463558-49.309168-49.309168-76.463758-114.86772-76.463758-184.60124 0-69.732521 27.155589-135.292072 76.463758-184.600241s114.86772-76.463758 184.600241-76.463758c69.733521 0 135.292072 27.155589 184.60124 76.463758 49.308169 49.309168 76.463558 114.86772 76.463558 184.600241 0 69.733521-27.155389 135.292072-76.463558 184.60124C647.292831 745.910465 581.73508 773.066053 512.001559 773.066053zM512.001559 308.950723c-111.962489 0-203.050532 91.088042-203.050532 203.050532s91.088042 203.050532 203.050532 203.050532 203.050532-91.088042 203.050532-203.050532S623.964048 308.950723 512.001559 308.950723zM163.915162 541.008388 105.900695 541.008388c-16.020138 0-29.007133-12.986995-29.007133-29.007333 0-16.020138 12.986995-29.007133 29.007133-29.007133l58.014466 0c16.020138 0 29.007133 12.986995 29.007133 29.007133C192.922295 528.021393 179.9353 541.008388 163.915162 541.008388zM918.101623 541.008388l-58.014466 0c-16.020338 0-29.007133-12.986995-29.007133-29.007333 0-16.020138 12.986995-29.007133 29.007133-29.007133l58.014466 0c16.020338 0 29.007133 12.986995 29.007133 29.007133C947.108756 528.021393 934.121761 541.008388 918.101623 541.008388zM512.001559 947.108253c-16.020338 0-29.007333-12.986995-29.007333-29.007133l0-58.014466c0-16.020338 12.986995-29.007133 29.007333-29.007133s29.007133 12.986995 29.007133 29.007133l0 58.014466C541.008892 934.121258 528.020898 947.108253 512.001559 947.108253zM512.001559 192.92279c-16.020338 0-29.007333-12.986995-29.007333-29.007133l0-58.014466c0-16.020338 12.986995-29.007133 29.007333-29.007133 16.020138 0 29.007133 12.986995 29.007133 29.007133l0 58.014466C541.008892 179.934796 528.020898 192.92279 512.001559 192.92279zM227.760275 828.165096c-7.423567 0-14.846934-2.83188-20.510694-8.49564-11.328319-11.328319-11.328319-29.694067 0-41.022387l41.022387-41.022387c11.328319-11.328319 29.695067-11.328319 41.022387 0 11.328319 11.328319 11.328319 29.694067 0 41.022387l-41.022387 41.022387C242.608208 825.333216 235.183841 828.165096 227.760275 828.165096zM761.050304 294.874067c-7.424366 0-14.846134-2.830881-20.511693-8.496439-11.32732-11.328319-11.32732-29.694067 0-41.022387l41.022387-41.022387c11.328319-11.32732 29.694067-11.328319 41.022387 0 11.32732 11.328319 11.32732 29.694067 0 41.022387l-41.022387 41.022387C775.898437 292.042187 768.473071 294.874067 761.050304 294.874067zM802.072691 828.165096c-7.423567 0-14.847134-2.83188-20.511693-8.49564l-41.022387-41.022387c-11.32732-11.328319-11.32732-29.694067 0-41.022387 11.328319-11.328319 29.694067-11.328319 41.022387 0l41.022387 41.022387c11.32732 11.328319 11.32732 29.694067 0 41.022387C816.919824 825.333216 809.496258 828.165096 802.072691 828.165096zM268.782661 294.874067c-7.423567 0-14.846934-2.83188-20.511693-8.49564l-41.022387-41.022387c-11.328319-11.328319-11.328319-29.695067 0-41.022387s29.694067-11.32732 41.022387 0l41.022387 41.022387c11.328319 11.328319 11.328319 29.695067 0 41.022387C283.629795 292.042187 276.206228 294.874067 268.782661 294.874067z"
                                p-id="26182"></path>
                        </svg>
                    </div>
                    <div className="notification-btn">
                        <svg t="1712466928164" className="icon" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="27160" width="200" height="200">
                            <path
                                d="M800 625.066667V448c0-117.333333-70.4-217.6-170.666667-262.4-4.266667-61.866667-55.466667-110.933333-117.333333-110.933333s-113.066667 49.066667-117.333333 110.933333c-100.266667 44.8-170.666667 145.066667-170.666667 262.4v177.066667c-57.6 46.933333-85.333333 110.933333-85.333333 185.6 0 17.066667 14.933333 32 32 32h206.933333c14.933333 61.866667 70.4 106.666667 134.4 106.666666s119.466667-44.8 134.4-106.666666H853.333333c17.066667 0 32-14.933333 32-32 0-76.8-27.733333-138.666667-85.333333-185.6zM512 138.666667c19.2 0 36.266667 10.666667 44.8 25.6-14.933333-2.133333-29.866667-4.266667-44.8-4.266667-14.933333 0-29.866667 2.133333-44.8 4.266667 8.533333-14.933333 25.6-25.6 44.8-25.6z m0 746.666666c-29.866667 0-55.466667-17.066667-66.133333-42.666666h134.4c-12.8 25.6-38.4 42.666667-68.266667 42.666666z m-307.2-106.666666c6.4-46.933333 29.866667-83.2 70.4-113.066667 8.533333-6.4 12.8-14.933333 12.8-25.6v-192c0-123.733333 100.266667-224 224-224S736 324.266667 736 448v192c0 10.666667 4.266667 19.2 12.8 25.6 40.533333 29.866667 64 66.133333 70.4 113.066667H204.8z"
                                p-id="27161"></path>
                        </svg>

                        <div className="notification-container">
                            <div className="top">
                                <div className="title">Notifications</div>
                                <div className="operation">
                                    <a href="#">Mark all as read</a>
                                </div>
                            </div>
                            <div className="center">
                                <div className="item">
                                    <div className="left">
                                        <svg t="1712474451782" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="39139" width="200" height="200">
                                            <path
                                                d="M1022.912 512v0.608C1022.912 795.04 793.952 1024 511.52 1024S0.128 795.04 0.128 512.608c0-215.424 133.184-399.744 321.728-475.072l3.456-1.216C380.448 13.536 444.448 0.288 511.552 0.288s131.104 13.248 189.536 37.248l-3.328-1.216C889.632 112.704 1022.816 296.8 1022.944 512zM314.816 404.224a225.088 225.088 0 0 0 107.232 196.608l0.96 0.544a173.12 173.12 0 0 0 88.288 23.872c31.392 0 60.864-8.256 86.368-22.72l-0.864 0.448c67.008-39.68 111.232-111.616 111.232-193.856l-0.064-5.12v0.256a197.92 197.92 0 1 0-393.408 0.896l-0.096-0.896z m198.56 585.568h0.416c148.8 0 281.664-68.32 368.896-175.328l0.672-0.864c-10.336-84.8-92.96-157.024-209.952-193.024a237.12 237.12 0 0 1-162.08 63.68 237.184 237.184 0 0 1-165.312-66.752l0.064 0.064c-113.824 32.48-197.152 98.72-216.704 178.112 87.584 118.272 226.624 194.112 383.392 194.112h0.672-0.032z"
                                                p-id="39140"></path>
                                        </svg>
                                    </div>
                                    <div className="center">
                                        <div className="name">Who am I</div>
                                        <div className="notification-desc">Metioned you in comment</div>
                                        <div className="time">10:51 PM Agust 21,2021</div>
                                    </div>
                                    <div className="right">
                                        <svg t="1712474022521" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="36232">
                                            <path d="M243.2 512m-83.2 0a1.3 1.3 0 1 0 166.4 0 1.3 1.3 0 1 0-166.4 0Z"
                                                p-id="36233"></path>
                                            <path d="M512 512m-83.2 0a1.3 1.3 0 1 0 166.4 0 1.3 1.3 0 1 0-166.4 0Z"
                                                p-id="36234"></path>
                                            <path d="M780.8 512m-83.2 0a1.3 1.3 0 1 0 166.4 0 1.3 1.3 0 1 0-166.4 0Z"
                                                p-id="36235"></path>
                                        </svg>
                                    </div>
                                </div>
                                <div className="item">
                                    <div className="left">
                                        <svg t="1712474451782" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="39139" width="200" height="200">
                                            <path
                                                d="M1022.912 512v0.608C1022.912 795.04 793.952 1024 511.52 1024S0.128 795.04 0.128 512.608c0-215.424 133.184-399.744 321.728-475.072l3.456-1.216C380.448 13.536 444.448 0.288 511.552 0.288s131.104 13.248 189.536 37.248l-3.328-1.216C889.632 112.704 1022.816 296.8 1022.944 512zM314.816 404.224a225.088 225.088 0 0 0 107.232 196.608l0.96 0.544a173.12 173.12 0 0 0 88.288 23.872c31.392 0 60.864-8.256 86.368-22.72l-0.864 0.448c67.008-39.68 111.232-111.616 111.232-193.856l-0.064-5.12v0.256a197.92 197.92 0 1 0-393.408 0.896l-0.096-0.896z m198.56 585.568h0.416c148.8 0 281.664-68.32 368.896-175.328l0.672-0.864c-10.336-84.8-92.96-157.024-209.952-193.024a237.12 237.12 0 0 1-162.08 63.68 237.184 237.184 0 0 1-165.312-66.752l0.064 0.064c-113.824 32.48-197.152 98.72-216.704 178.112 87.584 118.272 226.624 194.112 383.392 194.112h0.672-0.032z"
                                                p-id="39140"></path>
                                        </svg>
                                    </div>
                                    <div className="center">
                                        <div className="name">Who am I</div>
                                        <div className="notification-desc">Metioned you in comment</div>
                                        <div className="time">10:51 PM Agust 21,2021</div>
                                    </div>
                                    <div className="right">
                                        <svg t="1712474022521" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="36232">
                                            <path d="M243.2 512m-83.2 0a1.3 1.3 0 1 0 166.4 0 1.3 1.3 0 1 0-166.4 0Z"
                                                p-id="36233"></path>
                                            <path d="M512 512m-83.2 0a1.3 1.3 0 1 0 166.4 0 1.3 1.3 0 1 0-166.4 0Z"
                                                p-id="36234"></path>
                                            <path d="M780.8 512m-83.2 0a1.3 1.3 0 1 0 166.4 0 1.3 1.3 0 1 0-166.4 0Z"
                                                p-id="36235"></path>
                                        </svg>
                                    </div>
                                </div>
                            </div>
                            <div className="bottom">
                                <a href="#">Notification history</a>
                            </div>
                        </div>
                    </div>
                    <div className="more-button">
                        <svg t="1712467155153" className="icon" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="28987" width="200" height="200">
                            <path
                                d="M62.385351 714.233957l244.450455 0 0 244.451478L62.385351 958.685435 62.385351 714.233957 62.385351 714.233957zM62.385351 714.233957"
                                p-id="28988"></path>
                            <path
                                d="M388.321678 388.299677l244.449431 0 0 244.450455L388.321678 632.750131 388.321678 388.299677 388.321678 388.299677zM388.321678 388.299677"
                                p-id="28989"></path>
                            <path
                                d="M62.385351 388.299677l244.450455 0 0 244.450455L62.385351 632.750131 62.385351 388.299677 62.385351 388.299677zM62.385351 388.299677"
                                p-id="28990"></path>
                            <path
                                d="M388.321678 62.365396l244.449431 0 0 244.450455L388.321678 306.815851 388.321678 62.365396 388.321678 62.365396zM388.321678 62.365396"
                                p-id="28991"></path>
                            <path
                                d="M62.385351 62.365396l244.450455 0 0 244.450455L62.385351 306.815851 62.385351 62.365396 62.385351 62.365396zM62.385351 62.365396"
                                p-id="28992"></path>
                            <path
                                d="M714.253912 714.233957l244.451478 0 0 244.451478L714.253912 958.685435 714.253912 714.233957 714.253912 714.233957zM714.253912 714.233957"
                                p-id="28993"></path>
                            <path
                                d="M388.321678 714.233957l244.449431 0 0 244.451478L388.321678 958.685435 388.321678 714.233957 388.321678 714.233957zM388.321678 714.233957"
                                p-id="28994"></path>
                            <path
                                d="M714.253912 62.365396l244.451478 0 0 244.450455L714.253912 306.815851 714.253912 62.365396 714.253912 62.365396zM714.253912 62.365396"
                                p-id="28995"></path>
                            <path
                                d="M714.253912 388.299677l244.451478 0 0 244.450455L714.253912 632.750131 714.253912 388.299677 714.253912 388.299677zM714.253912 388.299677"
                                p-id="28996"></path>
                        </svg>

                        <div className="company-nav-container">
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712481974691" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="40147" width="200" height="200">
                                        <path
                                            d="M509.866667 2.133333C791.466667 2.133333 1019.733333 230.4 1019.733333 512c2.133333 283.733333-228.266667 512-509.866666 512S0 795.733333 0 514.133333 228.266667 2.133333 509.866667 2.133333z"
                                            fill="#0094FF" p-id="40148"></path>
                                        <path
                                            d="M791.466667 324.266667h-166.4v40.533333h166.4v-40.533333z m-81.066667 138.666666c-19.2 0-36.266667 6.4-46.933333 17.066667-12.8 10.666667-19.2 27.733333-19.2 46.933333h132.266666c-6.4-44.8-27.733333-64-66.133333-64z m4.266667 189.866667c12.8 0 27.733333-4.266667 40.533333-10.666667 12.8-6.4 21.333333-17.066667 25.6-27.733333H853.333333c-21.333333 66.133333-68.266667 100.266667-138.666666 100.266667-46.933333 0-83.2-14.933333-110.933334-42.666667-27.733333-27.733333-40.533333-66.133333-40.533333-113.066667 0-44.8 14.933333-83.2 42.666667-113.066666s64-44.8 108.8-44.8c29.866667 0 55.466667 6.4 78.933333 21.333333 21.333333 14.933333 38.4 34.133333 49.066667 57.6 10.666667 23.466667 17.066667 51.2 17.066666 81.066667v14.933333h-213.333333c0 23.466667 6.4 42.666667 19.2 55.466667 8.533333 12.8 25.6 21.333333 49.066667 21.333333z m-433.066667-17.066667h96c44.8 0 66.133333-17.066667 66.133333-53.333333 0-38.4-21.333333-57.6-64-57.6h-98.133333v110.933333z m0-174.933333h91.733333c17.066667 0 29.866667-4.266667 40.533334-12.8 10.666667-8.533333 14.933333-19.2 14.933333-36.266667 0-32-21.333333-46.933333-61.866667-46.933333h-85.333333v96zM192 296.533333h192c19.2 0 36.266667 2.133333 51.2 4.266667s27.733333 8.533333 40.533333 14.933333c12.8 6.4 21.333333 17.066667 29.866667 32 6.4 12.8 10.666667 29.866667 10.666667 49.066667 0 38.4-19.2 68.266667-55.466667 85.333333 25.6 6.4 42.666667 19.2 55.466667 36.266667 12.8 17.066667 19.2 40.533333 19.2 66.133333 0 17.066667-2.133333 32-8.533334 44.8-4.266667 12.8-12.8 23.466667-21.333333 34.133334-8.533333 8.533333-19.2 17.066667-32 23.466666-12.8 6.4-25.6 10.666667-38.4 12.8-12.8 2.133333-27.733333 4.266667-42.666667 4.266667H192V296.533333z"
                                            fill="#FFFFFF" p-id="40149"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Behance
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712483226876" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="41381" width="200" height="200">
                                        <path
                                            d="M636.95889226 347.01521255l29.28724038 0.97624135L746.29792301 267.93966356l3.90496536-34.16844713c-63.4556875-56.62199807-147.4124432-90.79044518-239.17912971-90.79044517-165.96102881 0-305.56354124 112.26775477-346.56567777 265.53764609 8.78617211-5.85744807 27.33475769-1.9524827 27.33475767-1.9524827l160.1035807-26.35851632s7.80993077-13.66737884 12.69113754-12.69113751c38.07341248-41.97837786 91.76668653-65.40817018 147.4124432-65.40817017 47.83582594 0 90.79044518 16.59610288 124.95889226 44.9071019z"
                                            fill="#EA4335" p-id="41382"></path>
                                        <path
                                            d="M858.56567777 408.51841735c-18.54858557-68.3368942-56.62199807-128.86385765-108.3627894-174.74720092l-113.24399611 113.24399612c44.90710189 37.09717114 74.19434229 92.74292785 74.19434229 155.222374v19.5248269c54.66951539 0 99.57661729 44.90710189 99.57661728 99.57661728s-44.90710189 99.57661729-99.57661728 99.57661727H512l-19.52482692 20.50106826v120.07768555l19.52482692 19.52482693h200.12947589c143.50747783 0 259.68019801-116.17272015 259.68019801-259.68019801 0-87.86172112-44.90710189-165.96102881-113.24399613-212.82061338z"
                                            fill="#4285F4" p-id="41383"></path>
                                        <path
                                            d="M311.87052411 881.99547009H512v-160.10358074H311.87052411c-14.64362017 0-28.31099903-2.92872403-41.00213654-8.78617209l-29.28724038 8.78617209-80.05179036 80.05179036-6.83368941 27.33475768c45.88334324 33.19220576 100.55285861 51.74079135 157.17485669 52.7170327z"
                                            fill="#34A853" p-id="41384"></path>
                                        <path
                                            d="M311.87052411 362.63507408c-143.50747783 0-259.68019801 116.17272015-259.68019801 259.68019799 0 83.95675573 40.02589516 159.12733936 102.50534132 206.96316532l116.17272015-116.17272013c-36.12092977-16.59610288-58.57448076-51.74079135-58.57448074-90.79044519 0-54.66951539 44.90710189-99.57661729 99.57661728-99.57661725 40.02589516 0 75.17058365 24.40603363 90.79044516 58.57448074l116.17272014-116.17272015c-46.85958459-62.47944614-122.03016823-102.50534131-206.9631653-102.50534133z"
                                            fill="#FBBC05" p-id="41385"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Cloud
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712483352074" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="42543" width="200" height="200">
                                        <path
                                            d="M244.224 643.84c0 59.221333-45.098667 107.264-100.778667 107.264C87.808 751.104 42.666667 703.061333 42.666667 643.84c0-59.221333 45.141333-107.264 100.778666-107.264h100.778667v107.264zM294.613333 643.84c0-59.306667 45.141333-107.306667 100.821334-107.306667 55.637333 0 100.778667 48.042667 100.778666 107.264v268.288c0 59.264-45.141333 107.306667-100.778666 107.306667-55.68 0-100.821333-48.042667-100.821334-107.306667v-268.288z"
                                            fill="#E01E5A" p-id="42544"></path>
                                        <path
                                            d="M395.392 214.613333c-55.637333 0-100.778667-48.042667-100.778667-107.306666C294.613333 48.042667 339.754667 0 395.392 0c55.68 0 100.821333 48.042667 100.821333 107.306667V214.613333H395.392zM395.392 268.245333c55.68 0 100.821333 48.085333 100.821333 107.306667 0 59.306667-45.141333 107.306667-100.821333 107.306667H143.445333C87.808 482.858667 42.666667 434.816 42.666667 375.552c0-59.221333 45.141333-107.306667 100.778666-107.306667h251.946667z"
                                            fill="#36C5F0" p-id="42545"></path>
                                        <path
                                            d="M798.549333 375.552c0-59.221333 45.098667-107.306667 100.778667-107.306667 55.637333 0 100.778667 48.085333 100.778667 107.306667 0 59.306667-45.141333 107.306667-100.778667 107.306667h-100.778667V375.552zM748.16 375.552c0 59.306667-45.141333 107.306667-100.821333 107.306667-55.637333 0-100.778667-48.042667-100.778667-107.306667V107.306667C546.56 48.042667 591.701333 0 647.338667 0c55.68 0 100.821333 48.042667 100.821333 107.306667v268.245333z"
                                            fill="#2EB67D" p-id="42546"></path>
                                        <path
                                            d="M647.381333 804.778667c55.637333 0 100.778667 48.042667 100.778667 107.306666 0 59.264-45.141333 107.306667-100.778667 107.306667-55.68 0-100.821333-48.042667-100.821333-107.306667v-107.306666h100.821333zM647.381333 751.104c-55.68 0-100.821333-48.042667-100.821333-107.306667 0-59.221333 45.141333-107.306667 100.821333-107.306666h251.904c55.68 0 100.778667 48.085333 100.778667 107.306666 0 59.306667-45.098667 107.306667-100.778667 107.306667h-251.904z"
                                            fill="#ECB22E" p-id="42547"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    &nbsp;&nbsp;&nbsp;&nbsp;Slack&nbsp;&nbsp;&nbsp;
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712483476613" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="43520" width="200" height="200">
                                        <path
                                            d="M932.317 567.767l-47.212-145.306-93.573-287.998c-4.813-14.817-25.777-14.817-30.591 0L667.36 422.46H356.628l-93.577-287.998c-4.813-14.817-25.776-14.817-30.593 0L138.885 422.46l-47.21 145.31a32.166 32.166 0 0 0 11.683 35.963l408.628 296.89 408.631-296.888a32.17 32.17 0 0 0 11.684-35.965"
                                            fill="#FC6D26" p-id="43521"></path>
                                        <path d="M512.002 900.628l155.365-478.17H356.635z" fill="#E24329" p-id="43522"></path>
                                        <path d="M512.004 900.628L356.64 422.472H138.902z" fill="#FC6D26" p-id="43523"></path>
                                        <path
                                            d="M138.891 422.466l-47.214 145.31a32.164 32.164 0 0 0 11.686 35.962l408.629 296.89z"
                                            fill="#FCA326" p-id="43524"></path>
                                        <path d="M138.893 422.46h217.738L263.053 134.46c-4.812-14.819-25.778-14.819-30.59 0z"
                                            fill="#E24329" p-id="43525"></path>
                                        <path d="M512.002 900.628l155.365-478.154h217.738z" fill="#FC6D26" p-id="43526"></path>
                                        <path
                                            d="M885.115 422.466l47.214 145.31a32.164 32.164 0 0 1-11.685 35.962l-408.63 296.89z"
                                            fill="#FCA326" p-id="43527"></path>
                                        <path d="M885.096 422.46H667.361l93.577-287.999c4.815-14.819 25.779-14.819 30.591 0z"
                                            fill="#E24329" p-id="43528"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    GitLab
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712483572183" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="46933" width="200" height="200">
                                        <path
                                            d="M32.91497 0.001463a32.182811 32.182811 0 0 0-25.014821 11.556555 34.01138 34.01138 0 0 0-7.460561 27.135961l137.947231 859.793057a44.617079 44.617079 0 0 0 43.30051 37.887946h661.941912c16.091406 0.219428 29.988529-11.70284 32.548524-28.086817L1014.19814 38.840265a34.01138 34.01138 0 0 0-7.460561-27.062819 32.182811 32.182811 0 0 0-25.087964-11.629698L32.988113 0.001463z m581.11917 621.420826h-211.382555L345.600238 314.515299h319.633829l-51.199927 306.833848z"
                                            fill="#2684FF" p-id="46934"></path>
                                        <path
                                            d="M970.312488 314.588442H665.234067l-51.199927 306.833847h-211.382555l-249.416787 303.908138c7.899417 7.021704 17.993117 10.971413 28.452531 10.971413h662.16134c16.091406 0.292571 29.988529-11.70284 32.475382-28.013675l93.988437-593.699723z"
                                            fill="#2684FF" p-id="46935"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Bitbucket
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712483656689" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="51333" width="200" height="200">
                                        <path
                                            d="M170.666667 981.333333l170.666666-298.666666h682.666667l-170.666667 298.666666z"
                                            fill="#4E91F5" p-id="51334"></path>
                                        <path d="M682.666667 682.666667h341.333333L682.666667 85.333333H341.333333z"
                                            fill="#FFD550" p-id="51335"></path>
                                        <path
                                            d="M0 682.666667l170.666667 298.666666 341.333333-597.333333-170.666667-298.666667z"
                                            fill="#23AE6F" p-id="51336"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Drive
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712483715832" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="53115" width="200" height="200">
                                        <path
                                            d="M896 0H128a128 128 0 0 0-128 128v768a128 128 0 0 0 128 128h768c70.656 0 128-57.344 128-128V128a128 128 0 0 0-128-128zM445.44 775.68c0 33.92-27.52 61.44-61.44 61.44H194.56c-33.92 0-61.44-27.552-61.44-61.44V194.56c0-33.92 27.52-61.44 61.44-61.44H384c33.92 0 61.44 27.52 61.44 61.44v581.12z m445.44-256c0 33.888-27.52 61.44-61.44 61.44H640c-33.92 0-61.44-27.552-61.44-61.44V194.56c0-33.92 27.552-61.44 61.44-61.44h189.44c33.92 0 61.44 27.52 61.44 61.44v325.12z"
                                            fill="#0079BF" p-id="53116"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Trello
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712483800370" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="54972" width="200" height="200">
                                        <path
                                            d="M341.333333 1024c94.208 0 170.666667-76.458667 170.666667-170.666667v-170.666666H341.333333c-94.208 0-170.666667 76.458667-170.666666 170.666666s76.458667 170.666667 170.666666 170.666667z"
                                            fill="#0ACF83" p-id="54973"></path>
                                        <path
                                            d="M170.666667 512c0-94.208 76.458667-170.666667 170.666666-170.666667h170.666667v341.333334H341.333333c-94.208 0-170.666667-76.458667-170.666666-170.666667z"
                                            fill="#A259FF" p-id="54974"></path>
                                        <path
                                            d="M170.666667 170.666667c0-94.208 76.458667-170.666667 170.666666-170.666667h170.666667v341.333333H341.333333C247.125333 341.333333 170.666667 264.874667 170.666667 170.666667z"
                                            fill="#F24E1E" p-id="54975"></path>
                                        <path
                                            d="M512 0h170.666667c94.208 0 170.666667 76.458667 170.666666 170.666667s-76.458667 170.666667-170.666666 170.666666h-170.666667V0z"
                                            fill="#FF7262" p-id="54976"></path>
                                        <path
                                            d="M853.333333 512c0 94.208-76.458667 170.666667-170.666666 170.666667s-170.666667-76.458667-170.666667-170.666667 76.458667-170.666667 170.666667-170.666667 170.666667 76.458667 170.666666 170.666667z"
                                            fill="#1ABCFE" p-id="54977"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Figma
                                </div>
                            </div>

                            <div className="item">
                                <div className="logo">
                                    <svg t="1712484101050" className="icon" viewBox="0 0 1143 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="56104" width="200" height="200">
                                        <path
                                            d="M1006.933333 153.6c45.511111-28.444444 85.333333-79.644444 102.4-136.533333-45.511111 28.444444-96.711111 51.2-147.911111 56.888889-39.822222-45.511111-102.4-73.955556-170.666666-73.955556-130.844444 0-233.244444 108.088889-233.244445 244.622222 0 17.066667 0 39.822222 5.688889 56.888889-193.422222-11.377778-364.088889-108.088889-483.555556-256-17.066667 34.133333-28.444444 79.644444-28.444444 125.155556 0 85.333333 34.133333 159.288889 96.711111 204.8-39.822222 0-108.088889-11.377778-108.088889-28.444445v5.688889c0 119.466667 85.333333 216.177778 193.422222 238.933333-17.066667 5.688889-39.822222 11.377778-56.888888 11.377778-17.066667 0-28.444444 0-45.511112-5.688889 28.444444 96.711111 113.777778 170.666667 216.177778 170.666667-79.644444 68.266667-182.044444 108.088889-290.133333 108.088889-17.066667 0-39.822222 0-56.888889-5.688889 102.4 68.266667 227.555556 108.088889 358.4 108.088889 426.666667 0 665.6-375.466667 665.6-699.733333v-34.133334c45.511111-34.133333 85.333333-79.644444 113.777778-125.155555-34.133333 17.066667-79.644444 28.444444-130.844445 34.133333z"
                                            fill="#1DA1F2" p-id="56105"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Twitter
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712484248457" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="59710" width="200" height="200">
                                        <path
                                            d="M887 77H137c-33.1 0-60 26.9-60 60v750c0 33.1 26.9 60 60 60h750c33.1 0 60-26.9 60-60V137c0-33.1-26.9-60-60-60zM253 753.6V396h123.2v357.6H253zM315.1 347v0.6c-36.4 1.6-67.3-26.5-69-62.9 1.9-36.3 32.7-64.3 69-62.6 36.3-1.6 67.1 26.3 69 62.6-2 36.1-32.8 63.9-69 62.3z m494.7 406.6H686V560.1c0-39.7-15.7-79.5-62.1-79.5-39.9 1.5-70.9 35-69.4 74.9 0.1 1.8 0.2 3.6 0.4 5.4v192.3H435.4V396h120.3v51.6c25.3-39.2 69.4-62.2 116-60.6 51.3 0 138.9 25.8 138.9 165.6l-0.8 201z"
                                            fill="#2D7BBD" p-id="59711"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Linkedin
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712484339024" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="62469" width="200" height="200">
                                        <path
                                            d="M634.5728 16.5888A358.8608 358.8608 0 0 0 526.6432 0a356.7104 356.7104 0 0 0-273.6128 127.488l168.704 141.9264L634.5728 16.5888z"
                                            fill="#1A73E8" p-id="62470"></path>
                                        <path
                                            d="M252.9792 127.5392a356.2496 356.2496 0 0 0-83.6608 229.5808c0 67.072 13.3632 121.6512 35.3792 170.3424l217.088-258.048L252.928 127.488z"
                                            fill="#EA4335" p-id="62471"></path>
                                        <path
                                            d="M526.6432 220.672a136.7552 136.7552 0 0 1 104.2944 224.9728l212.5824-252.8256A357.376 357.376 0 0 0 634.368 16.7936L421.9392 269.6192a136.3968 136.3968 0 0 1 104.704-48.896z"
                                            fill="#4285F4" p-id="62472"></path>
                                        <path
                                            d="M526.6432 493.9264a136.704 136.704 0 0 1-136.6528-136.6016 135.68 135.68 0 0 1 31.9488-87.7056l-217.2416 258.048c37.1712 82.2784 98.816 148.3264 162.304 231.5776l263.8848-313.6a136.0896 136.0896 0 0 1-104.2432 48.2816z"
                                            fill="#FBBC04" p-id="62473"></path>
                                        <path
                                            d="M625.664 844.8c119.296-186.368 258.048-271.0528 258.048-487.68 0-59.392-14.5408-115.4048-40.192-164.7104L367.2064 759.2448c20.1728 26.4704 40.6016 54.5792 60.416 85.7088C499.968 956.928 479.9488 1024 526.6432 1024c46.6944 0 26.6752-67.2768 99.0208-179.2z"
                                            fill="#34A853" p-id="62474"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Maps
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712484379225" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="64466" width="200" height="200">
                                        <path
                                            d="M752.512 948.736H257.536c-112.384 0-203.52-91.136-203.52-203.52V250.24c0-112.384 91.136-203.52 203.52-203.52h494.976c112.384 0 203.52 91.136 203.52 203.52v494.976c0 112.384-91.136 203.52-203.52 203.52z"
                                            fill="#ffffff" p-id="64467"></path>
                                        <path
                                            d="M516.352 471.68c-3.968-4.992-7.552-10.24-10.624-15.744-3.2 5.376-7.04 10.496-11.136 15.488 4.224 0.512 8.32 1.28 12.544 1.28 3.072-0.128 6.144-0.768 9.216-1.024z"
                                            fill="#FFA943" p-id="64468"></path>
                                        <path
                                            d="M488.704 319.744c7.68 7.68 13.568 16.512 18.56 25.6 4.736-8.064 10.112-15.744 17.024-22.656l81.792-81.792c3.712-3.712 7.808-6.656 11.776-9.728-6.656-55.552-53.504-98.816-110.848-98.816-56.832 0-103.168 42.24-110.72 97.024 3.456 2.816 7.168 5.248 10.368 8.576l82.048 81.792z"
                                            fill="#FF9A44" p-id="64469"></path>
                                        <path
                                            d="M488.704 478.464c2.176-2.176 3.84-4.736 5.888-7.168-6.4-0.768-12.672-1.92-18.688-3.712 1.536 6.144 2.56 12.416 3.072 18.816 3.2-2.56 6.656-4.864 9.728-7.936zM367.616 383.36c10.88 0 21.248 2.048 31.232 4.992-2.304-8.96-3.968-18.304-3.968-28.032v-115.712c0-5.248 0.896-10.24 1.536-15.232-44.032-34.56-107.648-32.128-148.224 8.576-40.192 40.192-43.008 102.912-9.728 146.816 4.48-0.512 8.832-1.408 13.44-1.408h115.712z"
                                            fill="#FF7769" p-id="64470"></path>
                                        <path
                                            d="M394.88 360.32c0 9.728 1.664 19.072 3.968 28.032 37.76 11.008 67.2 40.96 77.056 79.232 6.016 1.792 12.288 2.944 18.688 3.712 4.224-4.992 7.936-9.984 11.136-15.488-18.944-34.432-18.432-76.416 1.536-110.464-4.992-9.088-10.88-17.92-18.56-25.6l-81.792-81.792c-3.2-3.2-6.912-5.76-10.368-8.576-0.64 4.992-1.536 9.984-1.536 15.232v115.712z"
                                            fill="#FF5F1F" p-id="64471"></path>
                                        <path
                                            d="M478.976 486.4c-4.992 3.968-10.24 7.552-15.744 10.624 5.376 3.2 10.496 7.04 15.488 11.136 0.512-4.224 1.28-8.32 1.28-12.544-0.128-3.2-0.896-6.144-1.024-9.216zM327.04 514.048c7.68-7.68 16.512-13.568 25.6-18.56-8.064-4.736-15.744-10.112-22.656-17.024l-81.792-81.792c-3.712-3.712-6.656-7.808-9.728-11.776-55.552 6.656-98.816 53.504-98.816 110.848 0 56.832 42.24 103.168 97.024 110.72 2.816-3.456 5.248-7.168 8.448-10.368l81.92-82.048z"
                                            fill="#D684A9" p-id="64472"></path>
                                        <path
                                            d="M329.984 478.464c6.912 6.912 14.592 12.288 22.656 17.024 34.432-18.944 76.416-18.432 110.464 1.536 5.504-3.072 10.752-6.528 15.744-10.624-0.512-6.4-1.408-12.8-3.072-18.816-37.76-11.008-67.2-40.96-77.056-79.232-9.984-2.944-20.352-4.992-31.232-4.992h-115.712c-4.608 0-8.96 0.768-13.44 1.408 3.072 4.096 6.016 8.192 9.728 11.776l81.92 81.92z"
                                            fill="#DC2F1B" p-id="64473"></path>
                                        <path
                                            d="M398.848 388.352c9.856 38.272 39.296 68.224 77.056 79.232-9.856-38.272-39.296-68.224-77.056-79.232z"
                                            fill="#B1190A" p-id="64474"></path>
                                        <path
                                            d="M478.592 508.16c-0.768 6.4-1.92 12.672-3.712 18.688 6.144-1.536 12.416-2.56 18.816-3.072-2.56-3.328-4.992-6.784-7.936-9.728-2.304-2.176-4.864-3.84-7.168-5.888zM390.656 635.136c0-10.88 2.048-21.248 4.992-31.232-8.96 2.304-18.304 3.968-28.032 3.968h-115.712c-5.248 0-10.24-0.896-15.232-1.536-34.56 44.032-32.128 107.648 8.448 148.224 40.192 40.192 102.912 43.008 146.816 9.728-0.512-4.48-1.408-8.832-1.408-13.44v-115.712z"
                                            fill="#A48ABC" p-id="64475"></path>
                                        <path
                                            d="M367.616 607.872c9.728 0 19.072-1.664 28.032-3.968 11.008-37.76 40.96-67.2 79.232-77.056 1.792-6.016 2.944-12.288 3.712-18.688-4.992-4.224-9.984-7.936-15.488-11.136-34.432 18.944-76.416 18.432-110.464-1.536-9.088 4.992-17.92 10.88-25.6 18.56l-81.792 81.792c-3.2 3.2-5.76 6.912-8.448 10.368 4.992 0.64 9.984 1.536 15.232 1.536h115.584z"
                                            fill="#A54569" p-id="64476"></path>
                                        <path
                                            d="M352.64 495.488c34.048 20.096 76.032 20.48 110.464 1.536-34.048-20.096-76.032-20.48-110.464-1.536z"
                                            fill="#881309" p-id="64477"></path>
                                        <path
                                            d="M493.696 523.776c3.968 4.992 7.552 10.24 10.624 15.744 3.2-5.376 7.04-10.496 11.136-15.488-4.224-0.512-8.32-1.28-12.544-1.28-3.2 0.128-6.144 0.768-9.216 1.024z"
                                            fill="#55A3D5" p-id="64478"></path>
                                        <path
                                            d="M521.344 675.712c-7.68-7.68-13.568-16.512-18.56-25.6-4.736 8.064-10.112 15.744-17.024 22.656l-81.792 81.792c-3.712 3.712-7.808 6.656-11.776 9.728 6.656 55.552 53.504 98.816 110.848 98.816 56.832 0 103.168-42.24 110.72-97.024-3.456-2.816-7.168-5.248-10.368-8.448l-82.048-81.92z"
                                            fill="#5C9FD2" p-id="64479"></path>
                                        <path
                                            d="M485.76 672.768c6.912-6.912 12.288-14.592 17.024-22.656-18.944-34.432-18.432-76.416 1.536-110.464-3.072-5.504-6.528-10.752-10.624-15.744-6.4 0.512-12.8 1.408-18.816 3.072-11.008 37.76-40.96 67.2-79.232 77.056-2.944 9.984-4.992 20.352-4.992 31.232v115.712c0 4.608 0.768 8.96 1.408 13.44 4.096-3.072 8.192-6.016 11.776-9.728l81.92-81.92z"
                                            fill="#5D5092" p-id="64480"></path>
                                        <path
                                            d="M395.648 603.904c38.272-9.856 68.224-39.296 79.232-77.056-38.272 9.856-68.224 39.296-79.232 77.056z"
                                            fill="#4C0C2C" p-id="64481"></path>
                                        <path
                                            d="M515.456 524.16c6.4 0.768 12.672 1.92 18.688 3.712-1.536-6.144-2.56-12.416-3.072-18.816-3.328 2.56-6.784 4.992-9.728 7.936-2.304 2.176-3.84 4.864-5.888 7.168zM642.432 612.096c-10.88 0-21.248-2.048-31.232-4.992 2.304 8.96 3.968 18.304 3.968 28.032v115.712c0 5.248-0.896 10.24-1.536 15.232 44.032 34.56 107.648 32.128 148.224-8.448 40.192-40.192 43.008-102.912 9.728-146.816-4.48 0.512-8.832 1.408-13.44 1.408h-115.712z"
                                            fill="#57BA98" p-id="64482"></path>
                                        <path
                                            d="M615.168 635.136c0-9.728-1.664-19.072-3.968-28.032-37.76-11.008-67.2-40.96-77.056-79.232-6.016-1.792-12.288-2.944-18.688-3.712-4.224 4.992-7.936 9.984-11.136 15.488 18.944 34.432 18.432 76.416-1.536 110.464 4.992 9.088 10.88 17.92 18.56 25.6l81.792 81.792c3.2 3.2 6.912 5.76 10.368 8.448 0.64-4.992 1.536-9.984 1.536-15.232v-115.584z"
                                            fill="#236996" p-id="64483"></path>
                                        <path
                                            d="M502.784 650.112c20.096-34.048 20.48-76.032 1.536-110.464-20.096 34.048-20.48 76.032-1.536 110.464z"
                                            fill="#1B175B" p-id="64484"></path>
                                        <path
                                            d="M870.4 499.84c0-56.832-42.24-103.168-97.024-110.72-2.816 3.456-5.248 7.168-8.576 10.368L683.008 481.28c-7.68 7.68-16.512 13.568-25.6 18.56 8.064 4.736 15.744 10.112 22.656 17.024l81.792 81.792c3.712 3.712 6.656 7.808 9.728 11.776 55.552-6.4 98.816-53.248 98.816-110.592zM530.176 499.84c0 3.2 0.64 6.144 0.896 9.216 4.992-3.968 10.24-7.552 15.744-10.624-5.376-3.2-10.496-7.04-15.488-11.136-0.384 4.096-1.152 8.192-1.152 12.544z"
                                            fill="#ABCF61" p-id="64485"></path>
                                        <path
                                            d="M680.064 516.992c-6.912-6.912-14.592-12.288-22.656-17.024-34.432 18.944-76.416 18.432-110.464-1.536-5.504 3.072-10.752 6.528-15.744 10.624 0.512 6.4 1.408 12.8 3.072 18.816 37.76 11.008 67.2 40.96 77.056 79.232 9.984 2.944 20.352 4.992 31.232 4.992h115.712c4.608 0 8.96-0.768 13.44-1.408-3.072-4.096-6.016-8.192-9.728-11.776l-81.92-81.92z"
                                            fill="#209151" p-id="64486"></path>
                                        <path
                                            d="M611.2 607.104c-9.856-38.272-39.296-68.224-77.056-79.232 9.856 38.144 39.296 68.224 77.056 79.232z"
                                            fill="#003720" p-id="64487"></path>
                                        <path
                                            d="M619.264 360.32c0 10.88-2.048 21.248-4.992 31.232 8.96-2.304 18.304-3.968 28.032-3.968h115.712c5.248 0 10.24 0.896 15.232 1.536 34.56-44.032 32.128-107.648-8.576-148.224-40.192-40.192-102.912-43.008-146.816-9.728 0.512 4.48 1.408 8.832 1.408 13.44v115.712zM531.456 487.296c0.768-6.4 1.92-12.672 3.712-18.688-6.144 1.536-12.416 2.56-18.816 3.072 2.56 3.328 4.992 6.784 7.936 9.728 2.176 2.176 4.736 3.84 7.168 5.888z"
                                            fill="#F2DF44" p-id="64488"></path>
                                        <path
                                            d="M524.288 322.688c-6.912 6.912-12.288 14.592-17.024 22.656 18.944 34.432 18.432 76.416-1.536 110.464 3.072 5.504 6.528 10.752 10.624 15.744 6.4-0.512 12.8-1.408 18.816-3.072 11.008-37.76 40.96-67.2 79.232-77.056 2.944-9.984 4.992-20.352 4.992-31.232V244.48c0-4.608-0.768-8.96-1.408-13.44-4.096 3.072-8.192 6.016-11.776 9.728l-81.92 81.92z"
                                            fill="#D5B32C" p-id="64489"></path>
                                        <path
                                            d="M507.264 345.344c-20.096 34.048-20.48 76.032-1.536 110.464 19.968-34.048 20.48-76.032 1.536-110.464z"
                                            fill="#AB520E" p-id="64490"></path>
                                        <path
                                            d="M642.432 387.584c-9.728 0-19.072 1.664-28.032 3.968-11.008 37.76-40.96 67.2-79.232 77.056-1.792 6.016-2.944 12.288-3.712 18.688 4.992 4.224 9.984 7.936 15.488 11.136 34.432-18.944 76.416-18.432 110.464 1.536 9.088-4.992 17.92-10.88 25.6-18.56l81.792-81.792c3.2-3.2 5.76-6.912 8.576-10.368-4.992-0.64-9.984-1.536-15.232-1.536h-115.712z"
                                            fill="#81B028" p-id="64491"></path>
                                        <path
                                            d="M614.4 391.552c-38.272 9.856-68.224 39.296-79.232 77.056 38.144-9.856 68.224-39.296 79.232-77.056z"
                                            fill="#51800F" p-id="64492"></path>
                                        <path
                                            d="M546.944 498.432c34.048 20.096 76.032 20.48 110.464 1.536-34.048-19.968-76.032-20.48-110.464-1.536z"
                                            fill="#005007" p-id="64493"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Photos
                                </div>
                            </div>
                            <div className="item">
                                <div className="logo">
                                    <svg t="1712484410028" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="65490" width="200" height="200">
                                        <path
                                            d="M814.08 455.68C650.24 358.4 376.32 348.16 220.16 396.8c-25.6 7.68-51.2-7.68-58.88-30.72-7.68-25.6 7.68-51.2 30.72-58.88 181.76-53.76 481.28-43.52 670.72 69.12 23.04 12.8 30.72 43.52 17.92 66.56-12.8 17.92-43.52 25.6-66.56 12.8m-5.12 143.36c-12.8 17.92-35.84 25.6-53.76 12.8-138.24-84.48-348.16-110.08-509.44-58.88-20.48 5.12-43.52-5.12-48.64-25.6-5.12-20.48 5.12-43.52 25.6-48.64 186.88-56.32 417.28-28.16 576 69.12 15.36 7.68 23.04 33.28 10.24 51.2m-61.44 140.8c-10.24 15.36-28.16 20.48-43.52 10.24-120.32-74.24-271.36-89.6-450.56-48.64-17.92 5.12-33.28-7.68-38.4-23.04-5.12-17.92 7.68-33.28 23.04-38.4 194.56-43.52 363.52-25.6 496.64 56.32 17.92 7.68 20.48 28.16 12.8 43.52M512 0C230.4 0 0 230.4 0 512s230.4 512 512 512 512-230.4 512-512S796.16 0 512 0"
                                            fill="#1DB954" p-id="65491"></path>
                                    </svg>
                                </div>
                                <div className="name">
                                    Spotify
                                </div>
                            </div>
                        </div>
                    </div>
                    <div className="avatar">
                        <svg t="1712467371259" className="icon" viewBox="0 0 1024 1024" version="1.1"
                            xmlns="http://www.w3.org/2000/svg" p-id="30254" width="200" height="200">
                            <path
                                d="M512 448c-35.296 0-64-28.704-64-64s28.704-64 64-64 64 28.704 64 64-28.704 64-64 64m0-192c-70.592 0-128 57.408-128 128s57.408 128 128 128 128-57.408 128-128-57.408-128-128-128"
                                fill="#181818" p-id="30255"></path>
                            <path
                                d="M768 797.632v-69.92a151.904 151.904 0 0 0-151.712-151.68h-208.576A151.904 151.904 0 0 0 256 727.68v69.92C177.632 727.36 128 625.6 128 512 128 299.904 299.936 128 512 128s384 171.904 384 384c0 113.6-49.632 215.36-128 285.632m-448 46.56v-116.48a87.776 87.776 0 0 1 87.712-87.68h208.576A87.776 87.776 0 0 1 704 727.68v116.48a381.568 381.568 0 0 1-384 0m192-780.16C264.96 64 64 264.96 64 512c0 246.976 200.96 448 448 448s448-201.024 448-448c0-247.04-200.96-448-448-448"
                                p-id="30256"></path>
                        </svg>

                        <div className="profile-and-fun">
                            <div className="avatar">
                                <svg t="1712467371259" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                    xmlns="http://www.w3.org/2000/svg" p-id="30254" width="200" height="200">
                                    <path
                                        d="M512 448c-35.296 0-64-28.704-64-64s28.704-64 64-64 64 28.704 64 64-28.704 64-64 64m0-192c-70.592 0-128 57.408-128 128s57.408 128 128 128 128-57.408 128-128-57.408-128-128-128"
                                        fill="#181818" p-id="30255"></path>
                                    <path
                                        d="M768 797.632v-69.92a151.904 151.904 0 0 0-151.712-151.68h-208.576A151.904 151.904 0 0 0 256 727.68v69.92C177.632 727.36 128 625.6 128 512 128 299.904 299.936 128 512 128s384 171.904 384 384c0 113.6-49.632 215.36-128 285.632m-448 46.56v-116.48a87.776 87.776 0 0 1 87.712-87.68h208.576A87.776 87.776 0 0 1 704 727.68v116.48a381.568 381.568 0 0 1-384 0m192-780.16C264.96 64 64 264.96 64 512c0 246.976 200.96 448 448 448s448-201.024 448-448c0-247.04-200.96-448-448-448"
                                        p-id="30256"></path>
                                </svg>
                            </div>
                            <div className="name">Dexterleslie</div>
                            <div className="update-your-status-container">
                                <input type="text" placeholder="Update your status"/>
                            </div>
                            <div className="funs">
                                <div className="item">
                                    <div className="icon">
                                        <svg t="1712501531356" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="66630" width="200" height="200">
                                            <path
                                                d="M768 384c0-160-128-288-288-288S192 224 192 384c0 108.8 57.6 201.6 147.2 249.6-121.6 48-214.4 153.6-240 288-3.2 16 6.4 35.2 25.6 38.4h3.2c16 0 28.8-9.6 32-25.6C188.8 784 320 675.2 473.6 672h6.4c160 0 288-128 288-288z m-512 0c0-124.8 99.2-224 224-224s224 99.2 224 224c0 121.6-99.2 220.8-220.8 224h-9.6C352 604.8 256 505.6 256 384z m640 320H640c-19.2 0-32 12.8-32 32v192c0 19.2 12.8 32 32 32h256c19.2 0 32-12.8 32-32V736c0-16-12.8-32-32-32z m-32 192H672V768h192v128z"
                                                p-id="66631"></path>
                                            <path
                                                d="M736 864h32c19.2 0 32-12.8 32-32s-12.8-32-32-32h-32c-19.2 0-32 12.8-32 32s16 32 32 32z"
                                                p-id="66632"></path>
                                        </svg>
                                    </div>
                                    <div className="name">
                                        Profile
                                    </div>
                                </div>
                                <div className="item">
                                    <div className="icon">
                                        <svg t="1712509259389" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="70538" width="200" height="200">
                                            <path
                                                d="M924.8 385.6c-22.6-53.4-54.9-101.3-96-142.4-41.1-41.1-89-73.4-142.4-96C631.1 123.8 572.5 112 512 112s-119.1 11.8-174.4 35.2c-53.4 22.6-101.3 54.9-142.4 96-41.1 41.1-73.4 89-96 142.4C75.8 440.9 64 499.5 64 560c0 132.7 58.3 257.7 159.9 343.1l1.7 1.4c5.8 4.8 13.1 7.5 20.6 7.5h531.7c7.5 0 14.8-2.7 20.6-7.5l1.7-1.4C901.7 817.7 960 692.7 960 560c0-60.5-11.9-119.1-35.2-174.4zM761.4 836H262.6C184.5 765.5 140 665.6 140 560c0-99.4 38.7-192.8 109-263 70.3-70.3 163.7-109 263-109 99.4 0 192.8 38.7 263 109 70.3 70.3 109 163.7 109 263 0 105.6-44.5 205.5-122.6 276z"
                                                p-id="70539"></path>
                                            <path
                                                d="M623.5 421.5c-3.1-3.1-8.2-3.1-11.3 0L527.7 506c-18.7-5-39.4-0.2-54.1 14.5-21.9 21.9-21.9 57.3 0 79.2 21.9 21.9 57.3 21.9 79.2 0 14.7-14.7 19.5-35.4 14.5-54.1l84.5-84.5c3.1-3.1 3.1-8.2 0-11.3l-28.3-28.3zM490 320h44c4.4 0 8-3.6 8-8v-80c0-4.4-3.6-8-8-8h-44c-4.4 0-8 3.6-8 8v80c0 4.4 3.6 8 8 8zM750 538v44c0 4.4 3.6 8 8 8h80c4.4 0 8-3.6 8-8v-44c0-4.4-3.6-8-8-8h-80c-4.4 0-8 3.6-8 8zM762.7 340.8l-31.1-31.1c-3.1-3.1-8.2-3.1-11.3 0l-56.6 56.6c-3.1 3.1-3.1 8.2 0 11.3l31.1 31.1c3.1 3.1 8.2 3.1 11.3 0l56.6-56.6c3.1-3.1 3.1-8.2 0-11.3zM304.1 309.7c-3.1-3.1-8.2-3.1-11.3 0l-31.1 31.1c-3.1 3.1-3.1 8.2 0 11.3l56.6 56.6c3.1 3.1 8.2 3.1 11.3 0l31.1-31.1c3.1-3.1 3.1-8.2 0-11.3l-56.6-56.6zM262 530h-80c-4.4 0-8 3.6-8 8v44c0 4.4 3.6 8 8 8h80c4.4 0 8-3.6 8-8v-44c0-4.4-3.6-8-8-8z"
                                                p-id="70540"></path>
                                        </svg>
                                    </div>
                                    <div className="name">
                                        Dashboard
                                    </div>
                                </div>
                                <div className="item">
                                    <div className="icon">
                                        <svg t="1712509319047" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="73308" width="200" height="200">
                                            <path
                                                d="M894.419759 379.302769l-122.366955 0 0-101.96223c0-65.430198-25.470091-126.941134-71.723531-173.194574-46.25344-46.25344-107.764376-71.723531-173.194574-71.723531s-126.941134 25.470091-173.194574 71.723531-71.723531 107.764376-71.723531 173.194574l0 101.96223-122.366955 0c-11.307533 0-20.466124 9.168824-20.466124 20.466124l0 571.34255c0 11.2973 9.15859 20.466124 20.466124 20.466124l734.570121 0c11.307533 0 20.466124-9.168824 20.466124-20.466124l0-571.34255C914.885883 388.471592 905.727293 379.302769 894.419759 379.302769zM323.148841 277.340539c0-54.501288 21.213137-105.727996 59.740616-144.255475 38.517245-38.517245 89.743954-59.730383 144.245242-59.730383s105.727996 21.213137 144.245242 59.730383c38.527478 38.527478 59.740616 89.754187 59.740616 144.255475l0 101.96223-407.971715 0L323.148841 277.340539 323.148841 277.340539zM873.953635 950.645318l-693.637874 0 0-530.410302 693.637874 0L873.953635 950.645318z"
                                                p-id="73309"></path>
                                            <path
                                                d="M588.348875 644.6256c0 18.071587-7.971555 34.137495-20.404726 45.332465l0 56.691163c0 22.543436-18.276249 40.819684-40.809451 40.819684-22.533202 0-40.809451-18.276249-40.809451-40.819684l0-56.691163c-12.43317-11.19497-20.404726-27.260877-20.404726-45.332465 0-33.810037 27.40414-61.214177 61.214177-61.214177C560.954969 583.411423 588.348875 610.815563 588.348875 644.6256z"
                                                p-id="73310"></path>
                                        </svg>
                                    </div>
                                    <div className="name">
                                        Posts & Activity
                                    </div>
                                </div>
                                <div className="item">
                                    <div className="icon">
                                        <svg t="1712509473227" className="icon" viewBox="0 0 1024 1024" version="1.1" xmlns="http://www.w3.org/2000/svg" p-id="76253" width="200" height="200"><path d="M993.31142 624.247465l-94.120714-73.598993c1.237983-11.349845 2.21797-24.617663 2.21797-38.655472s-1.023986-27.305627-2.21797-38.655471l94.206712-73.640994c17.493761-13.823811 22.313695-38.357476 11.135848-58.793196l-97.876662-169.341685c-10.709854-19.497733-34.729525-28.415612-56.617226-20.351721l-111.060481 44.58539c-21.24771-15.35979-43.561404-28.329613-66.645089-38.697471l-16.895769-117.800389C652.666077 16.895769 633.210343 0 610.212657 0h-196.263316c-22.997686 0-42.45342 16.895769-45.183383 38.869469l-16.937768 118.228383c-22.357694 10.111862-44.329394 22.869687-66.55909 38.741471l-111.358478-44.713389c-21.289709-8.319886-45.525378 0.469994-56.191231 20.009726l-98.004661 169.597682c-11.561842 19.541733-6.825907 44.885386 11.093849 59.049192l94.120713 73.556995c-1.49398 14.421803-2.21797 26.921632-2.21797 38.655471s0.72599 24.233669 2.133971 38.655472l-94.206712 73.684992C13.18682 638.157275 8.406885 662.68894 19.542733 683.12666l97.876662 169.341685c10.623855 19.455734 34.601527 28.329613 56.617226 20.351722l111.060481-44.585391c21.24771 15.317791 43.605404 28.287613 66.687088 38.697471l16.895769 117.75839c2.773962 22.399694 22.229696 39.295463 45.225382 39.295463h196.263317c22.997686 0 42.45342-16.895769 45.183382-38.869468l16.937769-118.184385c22.357694-10.111862 44.329394-22.869687 66.55909-38.74147l111.358477 44.713389c21.20571 8.233887 45.525378-0.469994 56.191232-20.009727l98.388655-170.195673c10.881851-19.965727 6.101917-44.499392-11.475843-58.451201z m-25.813648 37.545487l-101.63061 171.431656-122.15233-49.065329c-6.911906-2.773962-14.761798-1.705977-20.735716 2.729963-26.453638 19.797729-52.009289 34.687526-78.206931 45.397379a21.319709 21.319709 0 0 0-13.055822 16.725771l-21.545705 132.308192-199.123278-2.26197-18.645745-130.004222a21.409707 21.409707 0 0 0-13.055821-16.725771c-27.263627-11.177847-53.503269-26.409639-78.036933-45.269382a21.24571 21.24571 0 0 0-13.013823-4.437939c-2.687963 0-5.375927 0.469994-7.97789 1.535979L154.664885 831.558631l-97.876661-169.341685c-0.895988-1.663977-0.72599-3.583951 0.297996-4.39394l103.550584-80.936893a21.377708 21.377708 0 0 0 8.02189-19.455734c-2.261969-17.877756-3.285955-32.341558-3.285955-45.439379s1.065985-27.519624 3.285955-45.439379a21.337708 21.337708 0 0 0-8.02189-19.455734l-104.058578-84.860839 101.630611-171.431657 122.15233 49.06533c6.953905 2.815962 14.761798 1.749976 20.777716-2.729963 26.409639-19.839729 52.009289-34.687526 78.206931-45.397379a21.405707 21.405707 0 0 0 13.013822-16.725772l21.589705-132.35019 199.165277 2.303968 18.601745 130.046222a21.409707 21.409707 0 0 0 13.055822 16.725772c27.221628 11.135848 53.461269 26.36764 78.036933 45.269381 6.015918 4.607937 13.90981 5.717922 20.949713 2.857961l125.652283-47.401352 97.876661 169.341684c0.895988 1.663977 0.72599 3.541952-0.297996 4.351941l-103.550584 80.936893a21.337708 21.337708 0 0 0-8.02189 19.455734c1.621978 12.969823 3.285955 28.54361 3.285955 45.439379 0 16.937768-1.663977 32.469556-3.285955 45.439379a21.377708 21.377708 0 0 0 8.02189 19.455734l103.422586 80.894894c1.105985 0.895988 1.319982 2.815962 0.635991 4.009945z" p-id="76254"></path><path d="M512.037999 298.661917c-117.630392 0-213.331083 95.700692-213.331083 213.331083s95.700692 213.331083 213.331083 213.331083S725.369083 629.623392 725.369083 511.993s-95.700692-213.331083-213.331084-213.331083z m0 383.99475c-94.120713 0-170.663667-76.542954-170.663666-170.663667s76.542954-170.663667 170.663666-170.663667 170.663667 76.542954 170.663667 170.663667-76.542954 170.663667-170.663667 170.663667z" p-id="76255"></path></svg>
                                    </div>
                                    <div className="name">
                                        Settings & Privacy
                                    </div>
                                </div>
                                <div className="item">
                                    <div className="icon">
                                        <svg t="1712509375570" className="icon" viewBox="0 0 1025 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="75274" width="200" height="200">
                                            <path
                                                d="M512.268258 1022.835842c-68.658678 0-135.399619-13.564433-198.369591-40.316509-60.752236-25.809077-115.373446-62.712976-162.346233-109.685763-46.971763-46.971763-83.875662-101.592974-109.685763-162.346233C15.115619 647.517366 1.551186 580.777449 1.551186 512.118771S15.115619 376.719151 41.866671 313.74918c25.810101-60.752236 62.714-115.373446 109.685763-162.346233 46.972787-46.971763 101.593997-83.875662 162.346233-109.685763 62.969971-26.751052 129.710912-40.315485 198.369591-40.315485s135.398595 13.564433 198.368567 40.315485c60.752236 25.810101 115.373446 62.714 162.346233 109.685763 46.971763 46.972787 83.875662 101.593997 109.685763 162.346233 26.752076 62.969971 40.316509 129.710912 40.316509 198.369591s-13.564433 135.398595-40.316509 198.368567c-25.809077 60.75326-62.712976 115.37447-109.685763 162.346233-46.971763 46.972787-101.592974 83.876686-162.346233 109.685763C647.666853 1009.27141 580.925912 1022.835842 512.268258 1022.835842zM512.268258 50.548195c-62.018782 0-122.293887 12.247716-179.152287 36.403219-54.923257 23.333323-104.317532 56.709936-146.810821 99.204249s-75.870926 91.888588-99.204249 146.810821c-24.155503 56.8584-36.403219 117.133505-36.403219 179.152287 0 62.017758 12.247716 122.292863 36.403219 179.152287 23.333323 54.923257 56.709936 104.317532 99.204249 146.811845 42.493289 42.493289 91.888588 75.870926 146.810821 99.204249 56.8584 24.155503 117.133505 36.403219 179.152287 36.403219 62.017758 0 122.292863-12.247716 179.152287-36.403219 54.923257-23.333323 104.317532-56.71096 146.811845-99.204249 42.493289-42.494313 75.870926-91.888588 99.204249-146.811845 24.155503-56.8584 36.403219-117.133505 36.403219-179.152287s-12.247716-122.293887-36.403219-179.152287c-23.334347-54.923257-56.71096-104.317532-99.205273-146.810821-42.493289-42.493289-91.887565-75.870926-146.810821-99.204249C634.561121 62.795911 574.286016 50.548195 512.268258 50.548195z"
                                                fill="#252334" p-id="75275"></path>
                                            <path
                                                d="M509.635849 662.039132c-12.806758 0.001024-23.601581-9.925544-24.490313-22.894076-3.937863-57.461468 9.411554-96.388564 21.305006-118.92428 10.555234-20.004671 31.440447-38.653719 41.477595-47.615787 1.05153-0.938903 1.925928-1.717056 2.568928-2.310909 52.08607-48.130801 51.437951-78.389684 51.308941-80.62073-0.051194-0.301022-0.097269-0.603068-0.136177-0.906139-3.954245-30.151375-17.538132-50.213384-41.527765-61.330731-13.282864-6.155599-29.039436-9.304046-46.834563-9.356264-0.558018-0.002048-1.022861-0.004096-1.388389-0.008191-1.139584-0.015358-2.262787-0.022525-3.434111-0.025597-37.411746 0.05017-62.803078 15.916297-77.620746 48.503496-11.747036 25.834675-12.07468 52.3574-12.07468 52.618491 0 13.5716-11.001648 24.573248-24.573248 24.573248s-24.573248-11.001648-24.573248-24.573248c0-1.498968 0.186347-37.121986 16.48353-72.962068 10.05353-22.10978 24.098165-39.887501 41.744829-52.838626 22.062681-16.19377 49.164926-24.425808 80.553154-24.467788 1.393508-0.008191 2.76449 0.011263 4.143664 0.029693l0.884637 0.004096c24.935703 0.072696 47.596333 4.7539 67.35732 13.912554 27.367431 12.683891 61.589774 40.134257 69.508503 98.920633 0.913306 5.3969 2.022174 19.359624-5.166525 39.883405-9.654215 27.562993-30.277313 56.07308-61.300014 84.739821-0.798631 0.737197-1.881901 1.706817-3.188379 2.87507-6.652183 5.939559-24.320348 21.715584-30.745229 33.892652-8.918041 16.901275-18.887613 46.689171-15.739165 92.629881 0.926616 13.53986-9.296879 25.268466-22.836738 26.195082C510.770314 662.019678 510.20001 662.039132 509.635849 662.039132zM602.014878 390.757643c0.002048 0.010239 0.004096 0.020478 0.006143 0.029693C602.018974 390.777097 602.016926 390.767882 602.014878 390.757643zM602.008735 390.731022c0.002048 0.007167 0.003072 0.014334 0.005119 0.021502C602.011807 390.74638 602.010783 390.738189 602.008735 390.731022zM601.795767 389.541267 601.795767 389.541267C601.795767 389.541267 601.795767 389.541267 601.795767 389.541267z"
                                                fill="#252334" p-id="75276"></path>
                                            <path
                                                d="M513.811253 706.462444c16.419025 0 29.729534 13.296175 29.729534 29.714176l0 7.870606c0 16.410834-13.195834 29.714176-29.729534 29.714176l0 0c-16.419025 0-29.729534-13.296175-29.729534-29.714176l0-7.870606C484.081719 719.765786 497.277553 706.462444 513.811253 706.462444L513.811253 706.462444z"
                                                p-id="75277"></path>
                                        </svg>
                                    </div>
                                    <div className="name">
                                        Help Center
                                    </div>
                                </div>
                            </div>
                            <div className="funs">
                                <div className="item">
                                    <div className="icon">
                                        <svg t="1712501705086" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                            xmlns="http://www.w3.org/2000/svg" p-id="67596" width="200" height="200">
                                            <path
                                                d="M524.8 21.504C361.984 21.504 230.4 153.6 230.4 316.928c0 111.616 61.952 209.408 153.088 259.584C166.912 628.224 0 804.352 0 998.4c0 0 0 25.6 26.624 25.6s24.576-25.6 24.576-25.6c0-200.704 212.48-385.536 460.8-385.536h12.8c162.816 0 294.4-132.608 294.4-295.936S687.616 21.504 524.8 21.504z m0 539.648C390.656 561.152 281.6 452.096 281.6 316.928c0-134.656 109.056-244.224 243.2-244.224S768 182.272 768 316.928c0 135.168-109.056 244.224-243.2 244.224zM1024.512 809.984c0 13.312-10.752 24.576-24.576 24.576h-163.84v163.84c0 13.312-10.752 24.576-24.576 24.576s-24.576-11.264-24.576-24.576v-163.84h-163.84c-13.312 0-24.576-11.264-24.576-24.576 0-13.312 11.264-24.576 24.576-24.576h163.84v-163.84c0-13.312 11.264-24.576 24.576-24.576s24.576 11.264 24.576 24.576v163.84h163.84c13.824 0 24.576 11.264 24.576 24.576z"
                                                p-id="67597"></path>
                                        </svg>
                                    </div>
                                    <div className="name">
                                        Add another account
                                    </div>
                                </div>
                            </div>
                            <div className="sign-out-container">
                                <button type="button">
                                    <svg t="1712506133208" className="icon" viewBox="0 0 1024 1024" version="1.1"
                                        xmlns="http://www.w3.org/2000/svg" p-id="68720" width="200" height="200">
                                        <path
                                            d="M886.613333 481.28l-219.306666-218.88a21.333333 21.333333 0 0 0-30.293334 0l-30.293333 30.293333a20.906667 20.906667 0 0 0 0 29.866667L753.92 469.333333H320a21.333333 21.333333 0 0 0-21.333333 21.333334v42.666666a21.333333 21.333333 0 0 0 21.333333 21.333334h433.92l-147.2 146.773333a21.333333 21.333333 0 0 0 0 30.293333l30.293333 29.866667a21.333333 21.333333 0 0 0 30.293334 0l219.306666-218.88a32 32 0 0 0 9.386667-22.613333v-16.213334a32.853333 32.853333 0 0 0-9.386667-22.613333z m-90.026666 30.72z m-348.586667 298.666667H213.333333V213.333333h234.666667a21.333333 21.333333 0 0 0 21.333333-21.333333v-42.666667a21.333333 21.333333 0 0 0-21.333333-21.333333H213.333333a85.333333 85.333333 0 0 0-85.333333 85.333333v597.333334a85.333333 85.333333 0 0 0 85.333333 85.333333h234.666667a21.333333 21.333333 0 0 0 21.333333-21.333333v-42.666667a21.333333 21.333333 0 0 0-21.333333-21.333333z"
                                            p-id="68721"></path>
                                    </svg>Sign out
                                </button>
                            </div>
                            <div className="privacy-and-terms-container">
                                <a href="#">Privacy policy</a>
                                <div className="dot">&#x2022;</div>
                                <a href="#">Terms</a>
                                <div className="dot">&#x2022;</div>
                                <a href="#">Cookies</a>
                            </div>
                        </div>
                    </div>
                </div>
            </header>
        )
    }
}
