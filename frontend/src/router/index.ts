import { createRouter, createWebHistory } from 'vue-router'
import Login from '../views/Login.vue'
import DashboardShell from '../views/DashboardShell.vue'
import DashboardMap from '../views/DashboardMap.vue'
import Incidents from '../views/Incidents.vue'
import Analytics from '../views/Analytics.vue'
import Users from '../views/Users.vue'
import { useAuthStore } from '../stores/auth.store'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false },
  },
  {
    path: '/',
    component: DashboardShell,
    meta: { requiresAuth: true },
    children: [
      { path: '', name: 'Dashboard', component: DashboardMap },
      { path: 'incidents', name: 'Incidents', component: Incidents },
      { path: 'analytics', name: 'Analytics', component: Analytics },
      {
        path: 'users',
        name: 'Users',
        component: Users,
        meta: { requiresRole: 'SUPER_USER' },
      },
    ],
  },
  {
    path: '/:pathMatch(.*)*',
    redirect: '/',
  },
]

const router = createRouter({
  history: createWebHistory(),
  routes,
})

router.beforeEach((to, _from, next) => {
  const authStore = useAuthStore()
  const isAuthenticated = authStore.isAuthenticated

  if (to.meta.requiresAuth && !isAuthenticated) {
    next('/login')
  } else if (to.name === 'Login' && isAuthenticated) {
    next('/')
  } else if (to.meta.requiresRole && to.meta.requiresRole !== authStore.user?.role) {
    // If the route requires a specific role and the user does not have it,
    // redirect to the dashboard.
    next({ name: 'Dashboard' })
  } else {
    next()
  }
})

export default router