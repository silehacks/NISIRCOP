import { useState } from 'react';

function LoginPage() {
  const [form, setForm] = useState({ email: '', password: '' });

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    // TODO: integrate authentication flow
    // eslint-disable-next-line no-alert
    alert(`Logging in ${form.email}`);
  };

  return (
    <div className="auth-wrapper">
      <form className="card auth-form" onSubmit={handleSubmit}>
        <h1>Sign in</h1>
        <label>
          Email
          <input type="email" name="email" value={form.email} onChange={handleChange} required />
        </label>
        <label>
          Password
          <input type="password" name="password" value={form.password} onChange={handleChange} required />
        </label>
        <button type="submit" className="primary">Access Command Center</button>
      </form>
    </div>
  );
}

export default LoginPage;
