import axios from 'axios'

const api = axios.create({ baseURL: '/api' })

export const applicationApi = {
  create: (data) => api.post('/applications', data),
  getAll: () => api.get('/applications'),
  getById: (id) => api.get(`/applications/${id}`),
  approve: (id, data) => api.put(`/applications/${id}/approve`, data),
  reject: (id, data) => api.put(`/applications/${id}/reject`, data),
  getPdfUrl: (id) => `/api/applications/${id}/pdf`
}
