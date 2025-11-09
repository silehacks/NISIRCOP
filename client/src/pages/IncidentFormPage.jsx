import { useState } from 'react';

const initialState = {
  title: '',
  description: '',
  type: 'theft',
  severity: 'medium',
  latitude: '',
  longitude: ''
};

function IncidentFormPage() {
  const [form, setForm] = useState(initialState);

  const handleChange = (event) => {
    const { name, value } = event.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    // TODO: integrate with API
    // eslint-disable-next-line no-alert
    alert('Incident submitted');
    setForm(initialState);
  };

  return (
    <div className="card">
      <h1>Report an Incident</h1>
      <form className="form" onSubmit={handleSubmit}>
        <label>
          Title
          <input name="title" value={form.title} onChange={handleChange} required />
        </label>
        <label>
          Description
          <textarea name="description" value={form.description} onChange={handleChange} required />
        </label>
        <label>
          Type
          <select name="type" value={form.type} onChange={handleChange}>
            <option value="theft">Theft</option>
            <option value="assault">Assault</option>
            <option value="traffic">Traffic</option>
            <option value="emergency">Emergency</option>
            <option value="other">Other</option>
          </select>
        </label>
        <label>
          Severity
          <select name="severity" value={form.severity} onChange={handleChange}>
            <option value="low">Low</option>
            <option value="medium">Medium</option>
            <option value="high">High</option>
            <option value="critical">Critical</option>
          </select>
        </label>
        <div className="grid two-columns">
          <label>
            Latitude
            <input name="latitude" value={form.latitude} onChange={handleChange} required />
          </label>
          <label>
            Longitude
            <input name="longitude" value={form.longitude} onChange={handleChange} required />
          </label>
        </div>
        <button type="submit" className="primary">Submit</button>
      </form>
    </div>
  );
}

export default IncidentFormPage;
