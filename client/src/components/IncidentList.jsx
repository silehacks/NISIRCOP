function IncidentList({ incidents = [] }) {
  if (incidents.length === 0) {
    return <p className="empty">No incidents reported yet.</p>;
  }

  return (
    <ul className="incident-list">
      {incidents.map((incident) => (
        <li key={incident.id}>
          <div className="incident-header">
            <span className={`badge badge-${incident.status}`}>{incident.status}</span>
            <span className="type">{incident.type}</span>
          </div>
          <h3>{incident.title}</h3>
          <time>{new Date(incident.reportedAt).toLocaleString()}</time>
        </li>
      ))}
    </ul>
  );
}

export default IncidentList;
