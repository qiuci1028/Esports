import { createRouter, createWebHashHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue'),
    meta: { title: '数据大屏' }
  },
  {
    path: '/player',
    name: 'PlayerQuery',
    component: () => import('@/views/PlayerQuery.vue'),
    meta: { title: '玩家档案' }
  },
  {
    path: '/champion',
    name: 'ChampionAnalysis',
    component: () => import('@/views/ChampionAnalysis.vue'),
    meta: { title: '英雄/特工分析' }
  },
  {
    path: '/match',
    name: 'MatchList',
    component: () => import('@/views/MatchList.vue'),
    meta: { title: '比赛列表' }
  },
  {
    path: '/match/:matchId',
    name: 'MatchDetail',
    component: () => import('@/views/MatchDetail.vue'),
    meta: { title: '比赛详情' }
  },
  { path: '/:pathMatch(.*)*', redirect: '/' }
]

const router = createRouter({
  history: createWebHashHistory(),
  routes
})

router.afterEach((to) => {
  const t = to.meta?.title
  document.title = t ? `${t} - Riot 电竞大数据分析平台` : 'Riot 电竞大数据分析平台'
})

export default router
