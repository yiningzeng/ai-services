import request from '../utils/request';
import { stringify } from 'qs';
const ip="aitest.api.qtingvision.com:888";//"10.50.102.166";
// const ip="localhost:8070";//"10.50.102.166";

export async function openS(port) {
  return request(`http://${ip}/pcb/service/${port}/open/`,{
    method: 'GET',
  });
}
export async function closeS(port) {
    return request(`http://${ip}/pcb/service/${port}/close/`,{
        method: 'GET',
    });
}
export async function searchS(port) {
    return request(`http://${ip}/pcb/service/${port}/status/`,{
        method: 'GET',
    });
}
export async function downloadExcel(params) {
    return request(`http://${ip}/pcb/service/${params.port}/excel/?${stringify(params)}`,{
        method: 'GET',
    });
}