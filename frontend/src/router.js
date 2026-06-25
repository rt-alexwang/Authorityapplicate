import { createRouter, createWebHistory } from 'vue-router'
import HomePage from './views/HomePage.vue'
import ApplyWizard from './views/ApplyWizard.vue'
import ApplySuccess from './views/ApplySuccess.vue'
import ReviewList from './views/ReviewList.vue'
import ReviewDetail from './views/ReviewDetail.vue'

const routes = [
  { path: '/', component: HomePage },
  { path: '/apply', component: ApplyWizard },
  { path: '/apply/success', component: ApplySuccess },
  { path: '/review', component: ReviewList },
  { path: '/review/:id', component: ReviewDetail }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
