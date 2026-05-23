import request from "@/utils/request";

export const queryPageApi = (name, begin, end, page, pageSize) =>
  request.get('/clazzs', { params: { name, begin, end, page, pageSize } })

export const addApi = (clazz) => request.post('/clazzs', clazz)

export const queryByIdApi = (id) => request.get(`/clazzs/${id}`)

export const updateApi = (clazz) => request.put('/clazzs', clazz)

export const deleteByIdApi = (id) => request.delete(`/clazzs/${id}`)

export const listApi = () => request.get('/clazzs/list')
