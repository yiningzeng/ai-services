import React from 'react';
import ReactDOM from 'react-dom';
import dva, { connect } from 'dva';
import { LocaleProvider, Modal, DatePicker, message, Upload, Icon, Spin, Button, Select, Input, Divider, Drawer,Typography } from 'antd';
const { Title, Paragraph, Text } = Typography;
// 由于 antd 组件的默认文案是英文，所以需要修改为中文
import zhCN from 'antd/lib/locale-provider/zh_CN';
import moment from 'moment';
import 'moment/locale/zh-cn';
import {getRecordByProjectId,startTest,openS,closeS,searchS,downloadExcel} from './services/api';
import copy from 'clipboard-copy';

const { RangePicker } = DatePicker;
const InputGroup = Input.Group;
const { Search } = Input;

moment.locale('zh-cn');

// const defaultPort=8101;

class MyUpload extends React.Component {
    //       localStorage.setItem("apiBaseUrl", this.state.apiBaseUrl);
    //         localStorage.setItem("apiPort", this.state.apiPort);
    state = {
        serviceChange: true,
        serviceStatus: undefined,
        loadingTip: "查询服务状态",
        fileList: [],
        apiBaseUrl: localStorage.getItem("apiBaseUrl") === null?"ai.api.qtingvision.com":localStorage.getItem("apiBaseUrl"),
        apiPort: localStorage.getItem("apiPort") === null?888:localStorage.getItem("apiPort"),
        port: undefined,//localStorage.getItem("serverPort") === null?8100:localStorage.getItem("serverPort"),
        assetsDir: "",
        startTime: moment().subtract(2, "hours"),
        endTime: moment(),
        javaUrl: "",
        javaPort: 888,
        projectId: undefined, // 判断是否已经包含项目，不包含弹出窗口让他输入
        showDrawerProjectId: false, // 用于显示projectid输入设置
        showLoading: false,
    };

    constructor(props) {
        super(props);
    }

    // componentDidMount(){

    // }

    componentDidMount() {
        try{
            const tableCon = ReactDOM.findDOMNode(this.refs['table']);
            const table = tableCon.querySelector('table');
            table.setAttribute('id','table-to-xls');
        }
        catch (e) {
            console.error(e);
        }

        MyUpload.getQueryVariable("err") ? Modal.error({
            title: '错误提示',
            content: '接口信息有误，请联系蜻蜓视觉提供最新的网址',
        }): undefined;
        this.setState({
            ...this.state,
            startTime: moment().subtract(2, "hours"),
            endTime: moment(),
            port: MyUpload.getQueryVariable("port"),
            assetsDir: MyUpload.getQueryVariable("assets"),
            apiBaseUrl: MyUpload.getQueryVariable("javaUrl"),
            apiPort: MyUpload.getQueryVariable("javaPort"),
            projectId: MyUpload.getQueryVariable("projectId") ? MyUpload.getQueryVariable("projectId") : undefined,
            showDrawerProjectId: MyUpload.getQueryVariable("projectId") ? false : true,
        },()=>{
            localStorage.setItem("apiBaseUrl", this.state.apiBaseUrl);
            localStorage.setItem("apiPort", this.state.apiPort);
            localStorage.setItem("serverPort", this.state.port);

            const {dispatch} = this.props;
            !this.state.showDrawerProjectId ? message.success(`注意！服务开启比较慢，请等待一会再查询\n正在查询服务开启状态...`) : undefined;
            dispatch({
                type: 'service/searchS',
                payload: this.state.port,
                callback: (v) => {
                    console.log(`${this.state.port}服务开启状态:${v}`);
                    if (v === 1) {
                        this.setState({...this.state,serviceChange: false, serviceStatus: true});
                        this.forceUpdate();
                    }
                    else {
                        this.setState({...this.state,serviceChange: false, serviceStatus: false});
                    }
                    // message.success(JSON.stringify(v));
                },
            });
        });

        // this.refs.table.refs.table.width='20%';
        // const tableCon = ReactDOM.findDOMNode(this.refs['tables']);
        // const table = tableCon.querySelector('table');
        // this.refs.table.setAttribute('id','table-to-xls');


    }

    datePickerOnChange = (value, dateString) => {
        console.log('Selected Time: ', value);
        console.log('Formatted Selected Time: ', dateString);
    }
      
    datePickerOnOk = (value) => {
        console.log('onOk: ', value);
        this.setState({
            ...this.state,
            startTime: value[0],
            endTime: value[1]
        });
    }

    handleChange = info => {
        let fileList = info.fileList;
        // message.success(JSON.stringify(fileList));
        // 1. Limit the number of uploaded files
        // Only to show two recent uploaded files, and old ones will be replaced by the new
        // fileList = fileList.slice(-2);

        // 2. Read from response and show file link
        fileList = fileList.map(file => {

            if (file.response) {
                file.name = file.response.data.fileBeforeName + " " + file.response.data.label_str;
                console.log(JSON.stringify(file.response));
                if (file.response.code == 0) {
                    file.uid = file.response.data.id;
                    if (file.response.data.num > 0) {
                        file.status = 'done';
                        file.url = encodeURI(file.response.data.url.replace("http://ai.qtingvision.com:888", ".."));
                    }
                    else {
                        file.name = file.response.data.fileBeforeName + " ok,";
                        file.status = 'done';
                        file.url = encodeURI(file.response.data.url.replace("http://ai.qtingvision.com:888", ".."));
                    }
                    // else file.status='error';
                }
                else {
                    file.status = 'error';
                    file.name = file.response.data.fileBeforeName + " (无法识别)";
                }
                // Component will show file.url as link
                // file.url = file.response.url;
            }
            return file;
        });
        // message.success(JSON.stringify(fileList));
        // 3. Filter successfully uploaded files according to response from server
        // fileList = fileList.filter(file => {
        //     if (file.response) {
        //         return file.response.status === "success";
        //     }
        //     return true;
        // });
        this.setState({...this.state,fileList});
    };

    closeOnChange=()=>{
        const {dispatch} = this.props;
        this.setState({...this.state,serviceChange: true});
        message.success(`正在关闭服务...`);
        dispatch({
            type: 'service/closeS',
            payload: this.state.port===undefined?8101:this.state.port,
            callback: (v) => {
                if (v === 1) {
                    let i = 0;
                    while (i < 3) {
                        this.timer = setTimeout(
                            () => {
                                dispatch({
                                    type: 'service/searchS',
                                    payload: this.state.port===undefined?8101:this.state.port,
                                    callback: (v) => {
                                        if (v === 0) {
                                            this.setState({...this.state,serviceChange:false,serviceStatus:false});
                                        }
                                        else {
                                            message.error("关闭失败");
                                            this.setState({...this.state,serviceChange:false,serviceStatus:true});
                                        }
                                    },
                                });
                            },
                            5000
                        );
                        i++;
                    }
                }
                else {
                    message.error("关闭失败");
                    this.setState({...this.state,serviceChange:false,serviceStatus:true});
                }
            },
        });
    }

    searchOnChange = () => {
        const {dispatch} = this.props;
        this.setState({...this.state,serviceChange: true});
        message.success(`正在查询服务....${this.state.port}`);
        // const { dispatch } = props;
        dispatch({
            type: 'service/searchS',
            payload: this.state.port===undefined?8101:this.state.port,
            callback: (v) => {
                if (v === 1) {
                    message.success("当前服务已经开启");
                    this.setState({...this.state,serviceChange:false,serviceStatus:true});
                }
                else{
                    message.success("当前服务已经关闭");
                    this.setState({...this.state,serviceChange:false,serviceStatus:false});
                }
            },
        });


    };

    selectHandleChange=(value)=> {
        this.setState({
            ...this.state,
            port:value,
            serviceChange: true
        });
        message.success(`正在查询服务....`);
        // const { dispatch } = props;
        const {dispatch} = this.props;
        dispatch({
            type: 'service/searchS',
            payload: value,
            callback: (v) => {
                if (v === 1) {
                    message.success("当前服务已经开启");
                    this.setState({...this.state,serviceChange:false,serviceStatus:true});
                }
                else {
                    message.success("当前服务已经关闭");
                    this.setState({...this.state,serviceChange:false,serviceStatus:false});
                }
            },
        });
        console.log(`selected ${value}`);
    };

    openOnChange = () => {
        const {dispatch} = this.props;
        this.setState({...this.state,serviceChange: true});

        message.success(`正在开启服务....`);
        // const { dispatch } = props;
        dispatch({
            type: 'service/openS',
            payload: this.state.port===undefined?8101:this.state.port,
            callback: (v) => {
                if (v === 1) {
                    let i = 0;
                    while (i < 3) {
                        this.timer = setTimeout(
                            () => {
                                // message.success(`开始执行查询${i}`);
                                dispatch({
                                    type: 'service/searchS',
                                    payload: this.state.port===undefined?8101:this.state.port,
                                    callback: (v) => {
                                        if (v === 1) {
                                            this.setState({...this.state,serviceChange:false,serviceStatus:true});
                                        }
                                        else {
                                            message.error("开启失败");
                                            this.setState({...this.state,serviceChange:false,serviceStatus:false});
                                        }
                                    },
                                });
                                },
                            5000
                        );
                        // setTimeout(message.success(`开始执行查询${i}`),1000);
                        if(this.state.serviceStatus===true)break;
                        i++;
                    }
                }
                else {
                    message.error("开启失败");
                    this.setState({...this.state,serviceChange:false,serviceStatus:false});
                }
            },
        });


    };

    static getQueryVariable(variable)
    {
        const query = window.location.search.substring(1);
        const vars = query.split("&");
        for (let i=0;i<vars.length;i++) {
            const pair = vars[i].split("=");
            if(pair[0] === variable){return pair[1];}
        }
        return(false);
    }

    render() {
        // 获得URL参数
        // message.success(MyUpload.getQueryVariable("assets"));
        // this.state.projectId ? message.success("项目id存在"):message.error("项目id不存在");
        const Dragger = Upload.Dragger;
        const pp = {
		action: `//${this.state.apiBaseUrl}:${this.state.apiPort}/pcb/testing?port=${this.state.port===undefined?8101:this.state.port}`,
            onChange: this.handleChange,
            multiple: true,
            accept: 'image/*',
        };
        console.log(`服务状态:${JSON.stringify(this.state.serviceStatus)}`);
        return (
            <div>
                <Drawer
                    title="开启在线测试"
                    placement="left"
                    width="100%"
                    height="300px"
                    closable={false}
                    onClose={()=>{this.setState({
                        ...this.state,
                        showDrawerProjectId: false,
                    })}}
                    visible={this.state.showDrawerProjectId}
                >
                    <Spin spinning={this.state.showLoading}  tip="正在开启测试服务...">
                    <Search
                        placeholder="输入您的服务id号"
                        enterButton="开启"
                        size="large"
                        onSearch={value => {
                            this.setState({
                               ...this.state,
                                showLoading: true,
                            });
                            const { dispatch } = this.props;
                            dispatch({
                                type: 'service/getRecordByProjectId',
                                payload: {
                                    projectId: value,
                                },
                                callback: (record) => {
                                    let port = 8100;
                                    let javaUrl = "ai.8101.api.qtingvision.com";
                                    let image = "registry.cn-hangzhou.aliyuncs.com/baymin/darknet-test:latest";
                                    switch (record.providerType) {
                                        case "yolov3":
                                            port = 8100;
                                            javaUrl = "ai.8101.api.qtingvision.com";
                                            image = "registry.cn-hangzhou.aliyuncs.com/baymin/darknet-test:latest";
                                            break;
                                        case "fasterRcnn":
                                            port = 8200;
                                            javaUrl = "ai.8201.api.qtingvision.com";
                                            image = "registry.cn-hangzhou.aliyuncs.com/baymin/detectron-test:latest"
                                            break;
                                        case "maskRcnn":
                                            port = 8300;
                                            javaUrl = "ai.8301.api.qtingvision.com";
                                            image = "registry.cn-hangzhou.aliyuncs.com/baymin/detectron-test:latest"
                                            break;
                                    }
                                    dispatch({
                                        type: 'service/startTest',
                                        payload: {
                                            providerType: record.providerType,
                                            assetsDir: record.assets, //nowAssetsDir
                                            weights: `/assets/${record.assets}/backup/yolov3-voc_last.weights`,
                                            port: port,
                                            javaUrl: javaUrl,
                                            javaPort: 888,
                                            image: image,
                                        },
                                        callback: (res) => {
                                            if (res.res !== "ok") {
                                                message.error(res.msg);
                                                this.setState({
                                                    ...this.state,
                                                    showLoading: false,
                                                });
                                            }
                                            else {
                                                setTimeout(function () {
                                                    const url = window.location.href.replace("#/", "");
                                                    window.location.href = window.location.href.indexOf("?") > 0 ?
                                                        `${url}&projectId=${value}&providerType=${record.providerType}&assets=${record.assets}`
                                                        : `${url}?javaUrl=${javaUrl}&javaPort=888&port=${port}&projectId=${value}&providerType=${record.providerType}&assets=${record.assets}`;
                                                    this.setState({
                                                        ...this.state,
                                                        showLoading: false,
                                                    }, ()=>{
                                                        window.location.href = window.location.href.indexOf("?") > 0 ?
                                                            `${url}&projectId=${value}&providerType=${record.providerType}&assets=${record.assets}`
                                                            : `${url}?javaUrl=${javaUrl}&javaPort=888&port=${port}&projectId=${value}&providerType=${record.providerType}&assets=${record.assets}`;
                                                    });
                                                }, 10000);
                                            }
                                        }});
                                },
                            });




                            // http://server.qtingvision.com:888/get_record_by_project_id
                        }
                        }
                    />
                        <Typography style={{marginTop: "20px"}}>
                            <Title>说明</Title>
                            <Paragraph>
                                在上面输入框中输入您的服务id号，然后点击开启按钮。服务加载比较耗时，大概需要10秒左右。如果进入到测试页面状态显示关闭，请点击状态页面的刷新按钮刷新即可，无需重复开启。
                            </Paragraph>
                        </Typography>
                    </Spin>
                </Drawer>
                <LocaleProvider locale={zhCN}>
                    <Spin spinning={this.state.serviceChange}
                          tip={`loading.....${this.state.loadingTip === undefined ? "查询服务状态" : this.state.loadingTip}`}>
                        <div style={{width: '50%', margin: '100px auto'}}>
                            <div>
                                <Button type="primary" size='large' onClick={()=>{
                                    location.reload()
                                }}>刷新页面</Button>
                                <Button style={{ marginLeft: "20px" }}  type="primary" size='large' onClick={()=>{
                                    const test=window.location.href.replace(window.location.host, "ai.test.qtingvision.com:888").split("?")[0];
                                    copy(test);
                                    Modal.success({
                                        title: '外网地址已成功复制到剪贴板，如果复制失败，请手动复制下面的连接',
                                        content: test,
                                    });
                                }}>复制外网地址</Button>
                            </div>
                            <Divider>服务</Divider>
                            检测服务端口:
                            <Select defaultValue={this.state.port} value={this.state.port} style={{ width: 120 }} onChange={this.selectHandleChange}>
                                <Option value="8097">8097</Option>
                                <Option value="8098">8098</Option>
                                <Option value="8099">8099</Option>
                                <Option value="8100">8100</Option>
                                <Option value="8101">8101</Option>
                                <Option value="8102">8102</Option>
                                <Option value="8103">8103</Option>
                                <Option value="8104">8104</Option>
                                <Option value="8105">8105</Option>
                                <Option value="8106">8106</Option>
                                <Option value="8888">8888</Option>
                            </Select>
                            <div>
                                服务状态:{this.state.serviceStatus===undefined?<div><Icon type="mth" theme="twoTone" twoToneColor="#ff8c00"/>未知</div>:this.state.serviceStatus===true?<div><Icon type="smile" theme="twoTone" twoToneColor="#52c41a"/>开启</div>: <div><Icon type="frown" theme="twoTone" twoToneColor="#eb2f96"/>关闭</div>}
                            </div>
                            <div>
                                {(this.state.serviceStatus!==undefined&&this.state.serviceStatus===true?
                                    <Button type="primary" size="small" onClick={this.closeOnChange} disabled={true}>关闭</Button>:
                                    <Button type="primary" size="small" onClick={this.openOnChange} disabled={true}>开启</Button>)}
                                <Button id="table-to-xls" style={{ marginLeft: 8 }} type="primary" size="small" onClick={this.searchOnChange}>手动查询服务状态</Button>
                                <Button style={{ marginLeft: 8 }} type="primary" size="small" onClick={()=>{
                                    localStorage.setItem("apiBaseUrl", this.state.apiBaseUrl);
                                    localStorage.setItem("apiPort", this.state.apiPort);
                                    localStorage.setItem("serverPort", this.state.port);
                                    message.success("保存成功");
                                }}>保存接口信息</Button>
                            </div>
                            <div style={{display: "none"}}>
                                接口地址:
                                <InputGroup compact>
                                    <Input style={{ width: '40%' }} addonBefore="http://" defaultValue={localStorage.getItem("apiBaseUrl") === null?this.state.apiBaseUrl:localStorage.getItem("apiBaseUrl")}
                                           onChange={e=>{
                                               this.setState({
                                                   ...this.state,
                                                   apiBaseUrl: e.target.value,
                                               });
                                           }}
                                           placeholder="网址不带http://" allowClear/>
                                    <Input
                                        style={{
                                            width: 30,
                                            borderLeft: 0,
                                            pointerEvents: 'none',
                                            backgroundColor: '#fff',
                                        }}
                                        placeholder=":"
                                        disabled
                                    />
                                    <Input style={{ width: '10%', textAlign: 'center', borderLeft: 0 }} onChange={(e)=>{
                                        this.setState({
                                            ...this.state,
                                            apiPort: e.target.value,
                                        });
                                    }} defaultValue={localStorage.getItem("apiPort") === null?this.state.apiPort:localStorage.getItem("apiPort")} placeholder="port" />
                                </InputGroup>
                            </div>
                            <div>
                                选择导出时间:
                                <br/>
                                <RangePicker
                                    defaultValue={[this.state.startTime, this.state.endTime]}
                                    showTime={{ format: 'HH:mm' }}
                                    format="YYYY-MM-DD HH:mm"
                                    placeholder={['Start Time', 'End Time']}
                                    onChange={this.datePickerOnChange}
                                    onOk={this.datePickerOnOk}
                                />
                                <Button style={{ marginLeft: 8 }} type="primary" size="small" onClick={()=>{
                                    message.success("正在处理请等待..."+this.state.endTime.format('YYYY-MM-DD HH:mm:ss'));
                                    const {dispatch} = this.props;
                                    dispatch({
                                        type: 'service/download',
                                        payload: {
                                            port: this.state.port===undefined?8101:this.state.port,
                                            startTime: this.state.startTime.format('YYYY-MM-DD HH:mm:ss'),
                                            endTime: this.state.endTime.format('YYYY-MM-DD HH:mm:ss')
                                        },
                                        callback: (v) => {
                                            // message.success(`${JSON.stringify(v)}`);
                                            window.location.href= v.data;
                                            // window
                                            // window.open(v.data, '_blank').location;
                                        },
                                    });
                                    window.location.href= v.data;
                                }}>导出</Button>
                            </div>
                            <Dragger {...pp} fileList={this.state.fileList}>
                                <p className="ant-upload-drag-icon">
                                    <Icon type="inbox"/>
                                </p>
                                <p className="ant-upload-text">{`上传图片${this.state.port === 8097 ? 1 : this.state.port === 8098 ? 2 : this.state.port === 8099 ? 3 : this.state.port === 8100 ? 4 : ""}`}</p>
                                <p className="ant-upload-hint">批量上传图片请点击或者拖放文件到该区域,支持单个或者批量的文件</p>
                            </Dragger>
                        </div>
                    </Spin>
                </LocaleProvider>
            </div>
        );
    }
}

// 1. Initialize
const app = dva();
console.log(2);
// 2. Model
// app.model(require('./src/models/service').default);
app.model({
    namespace: 'service',
    state: {
        record: {
            res: undefined,
            providerType: undefined,
            assets: undefined,
        },
        testRes: {
            res: '',
        },
    },
    effects: {
        *getRecordByProjectId({ payload ,callback}, { call, put }) {
            const response = yield call(getRecordByProjectId, payload);
            yield put({
                type: 'record',
                payload: response,
            });
            if (callback) callback(response);
        },
        *startTest({ payload,callback}, { call, put }) {
            const response = yield call(startTest,payload);
            yield put({
                type: 'testRes',
                payload: response,
            });
            if (callback)callback(response);
        },
        *openS({ payload ,callback}, { call, put }) {
            const response = yield call(openS, payload);
            if (callback) callback(response);
        },
        *closeS({ payload ,callback}, { call, put }) {
            const response = yield call(closeS, payload);
            if (callback) callback(response);
        },
        *searchS({ payload ,callback}, { call, put }) {
            const response = yield call(searchS, payload);
            if (callback) callback(response);
        },    
        *download({ payload ,callback}, { call, put }) {
            const response = yield call(downloadExcel, payload);
            if (callback) callback(response);
        },
    },
    reducers: {
        record(state, action) {
            return {
                ...state,
                record: action.payload,
            };
        },
        testRes(state, action) {
            return {
                ...state,
                testRes: action.payload,
            };
        },
    },
});
// 3. View
const App = connect(({ service }) => ({
    service
}))(function(props) {

    const { dispatch } = props;
    return (
        <div>
            <MyUpload dispatch={dispatch}/>
        </div>
    );
});

// 4. Router
app.router(() => <App />);

// 5. Start
app.start('#root');



// ReactDOM.render(<App />, document.getElementById('root'));
