import axios from 'axios'

const api = axios.create({
  baseURL: process.env.VUE_APP_API_URL || 'http://localhost:8080/api',
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
})

api.interceptors.response.use(
  response => response.data,
  error => {
    if (error.code === 'ERR_NETWORK') {
      console.error('Erro de rede:', error)
      throw { 
        message: 'Erro de conexão - verifique sua internet',
        original: error 
      }
    }
    
    if (error.response) {
      console.error('Erro na resposta:', {
        status: error.response.status,
        data: error.response.data,
        headers: error.response.headers
      })
      
      if (error.response.status === 500) {
        throw {
          message: 'Erro interno no servidor',
          details: error.response.data
        }
      }
      
      throw error.response.data
    }
    
    console.error('Erro desconhecido:', error)
    throw error
  }
)

export default api