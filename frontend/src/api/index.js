import axios from 'axios'

const api = axios.create({ baseURL: '/authority/api' })

export const createApplication = (data) => api.post('/applications', data)

export const optionsApi = {
  getAll: () => api.get('/options'),
  get: (type) => api.get(`/options/${type}`),
  add: (type, value) => api.post(`/options/${type}`, { value })
}

export const applicationApi = {
  create: (data) => api.post('/applications', data),
  getAll: () => api.get('/applications'),
  getById: (id) => api.get(`/applications/${id}`),
  approve: (id, data) => api.put(`/applications/${id}/approve`, data),
  reject: (id, data) => api.put(`/applications/${id}/reject`, data),
  getPdfUrl: (id) => `/authority/api/applications/${id}/pdf`,
  search: (q) => api.get('/applications/search', { params: { q } })
}
