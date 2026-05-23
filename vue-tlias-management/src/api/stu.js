import request from "@/utils/request";

export const queryPageApi = (name, degree, clazzId, page, pageSize) =>
  request.get('/students', { params: { name, degree, clazzId, page, pageSize } })

export const addApi = (student) => request.post('/students', student)

export const queryByIdApi = (id) => request.get(`/students/${id}`)

export const updateApi = (student) => request.put('/students', student)

export const deleteByIdApi = (ids) => request.delete(`/students/${ids}`)

export const violationApi = (id, score) => request.put(`/students/violation/${id}/${score}`)
