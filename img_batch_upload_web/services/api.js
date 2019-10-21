import request from '../utils/request';
import { stringify } from 'qs';
// const ip="aitest.api.qtingvision.com:888";//"10.50.102.166";
// const ip="ai.api.qtingvision.com:888";//"10.50.102.166";

export async function getRecordByProjectId(params) {
    return request(`http://server.qtingvision.com:888/get_record_by_project_id`, {
        method: 'POST',
        body: params
    });
}

export async function startTest(params) {
    return request(`http://server.qtingvision.com:888/start_test`, {
        method: 'POST',
        body: params
    });
}

export async function openS(port) {
    const apiBaseUrl = localStorage.getItem("apiBaseUrl") === null ? "ai.api.qtingvision.com" : localStorage.getItem("apiBaseUrl");
    const apiPort = localStorage.getItem("apiPort") === null ? 888 : localStorage.getItem("apiPort");
    const ip = apiBaseUrl + ":" + apiPort;
    return request(`http://${ip}/pcb/service/${port}/open/`, {
        method: 'GET',
    });
}
export async function closeS(port) {
    const apiBaseUrl = localStorage.getItem("apiBaseUrl") === null?"ai.api.qtingvision.com":localStorage.getItem("apiBaseUrl");
    const apiPort = localStorage.getItem("apiPort") === null?888:localStorage.getItem("apiPort");
    const ip = apiBaseUrl+":"+apiPort;
    return request(`http://${ip}/pcb/service/${port}/close/`,{
        method: 'GET',
    });
}
export async function searchS(port) {
    const apiBaseUrl = localStorage.getItem("apiBaseUrl") === null?"ai.api.qtingvision.com":localStorage.getItem("apiBaseUrl");
    const apiPort = localStorage.getItem("apiPort") === null?888:localStorage.getItem("apiPort");
    const ip = apiBaseUrl+":"+apiPort;
    return request(`http://${ip}/pcb/service/${port}/status/`,{
        method: 'GET',
    });
}
export async function downloadExcel(params) {
    const apiBaseUrl = localStorage.getItem("apiBaseUrl") === null?"ai.api.qtingvision.com":localStorage.getItem("apiBaseUrl");
    const apiPort = localStorage.getItem("apiPort") === null?888:localStorage.getItem("apiPort");
    const ip = apiBaseUrl+":"+apiPort;
    return request(`http://${ip}/pcb/service/${params.port}/excel/?${stringify(params)}`,{
        method: 'GET',
    });
}