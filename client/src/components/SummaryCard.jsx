function SummaryCard({ title, value, description }) {
  return (
    <article className="summary-card">
      <h3>{title}</h3>
      <p className="value">{value}</p>
      <p className="description">{description}</p>
    </article>
  );
}

export default SummaryCard;
