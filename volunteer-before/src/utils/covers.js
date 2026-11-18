import medical from '../assets/covers/medical.jpg'
import culture from '../assets/covers/culture.jpg'
import publicity from '../assets/covers/publicity.jpg'
import community from '../assets/covers/community.jpg'
import education from '../assets/covers/education.jpg'
import environment from '../assets/covers/environment.jpg'
import rescue from '../assets/covers/rescue.jpg'

export const COVERS = [
  { key: 'medical', name: '医疗健康 · 义诊', url: medical },
  { key: 'culture', name: '文化服务 · 图书', url: culture },
  { key: 'publicity', name: '公益宣传 · 献血', url: publicity },
  { key: 'community', name: '社区服务 · 敬老', url: community },
  { key: 'education', name: '教育助学 · 课堂', url: education },
  { key: 'environment', name: '环境保护 · 河岸', url: environment },
  { key: 'rescue', name: '应急救援 · 演练', url: rescue }
]

const CATEGORY_COVER = {
  医疗健康: 'medical',
  文化服务: 'culture',
  公益宣传: 'publicity',
  社区服务: 'community',
  教育助学: 'education',
  环境保护: 'environment',
  应急救援: 'rescue'
}

export function coverUrl(cover, category) {
  if (cover && /^(https?:|data:)/.test(cover)) return cover
  const byKey = COVERS.find((c) => c.key === cover)
  if (byKey) return byKey.url
  const fallback = COVERS.find((c) => c.key === CATEGORY_COVER[category])
  return (fallback || COVERS[3]).url
}

export const coverName = (key) => COVERS.find((c) => c.key === key)?.name || '自定义图片'
