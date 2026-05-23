import request from "@/utils/request";

export const queryPageApi = (name, gender, begin, end, page, pageSize) =>
  request.get('/emps', { params: { name, gender, begin, end, page, pageSize } })

export const addApi = (emp) => request.post('/emps', emp)

export const queryByIdApi = (id) => request.get(`/emps/${id}`)

export const updateApi = (emp) => request.put('/emps', emp)

export const deleteByIdApi = (ids) => request.delete(`/emps?ids=${ids}`)

export const listApi = () => request.get('/emps/list')
