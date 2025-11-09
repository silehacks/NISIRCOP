import { NavLink, Outlet } from 'react-router-dom';

const navItems = [
  { to: '/', label: 'Dashboard' },
  { to: '/incidents/new', label: 'Report Incident' }
];

function Layout() {
  return (
    <div className="app-shell">
      <aside className="sidebar">
        <div className="brand">NISIRCOP</div>
        <nav>
          <ul>
            {navItems.map((item) => (
              <li key={item.to}>
                <NavLink to={item.to} end>
                  {item.label}
                </NavLink>
              </li>
            ))}
          </ul>
        </nav>
      </aside>
      <main className="content">
        <Outlet />
      </main>
    </div>
  );
}

export default Layout;
