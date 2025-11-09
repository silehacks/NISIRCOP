import SummaryCard from '../components/SummaryCard.jsx';
import IncidentList from '../components/IncidentList.jsx';

const mockSummary = [
  { title: 'Active Incidents', value: 8, description: 'Across all districts' },
  { title: 'Units Available', value: 24, description: 'Ready for deployment' },
  { title: 'Avg. Response Time', value: '7m', description: 'Past 24 hours' }
];

const mockIncidents = [
  {
    id: '1',
    title: 'Vehicle theft at Bole',
    status: 'assigned',
    type: 'theft',
    reportedAt: '2024-05-21T10:00:00Z'
  },
  {
    id: '2',
    title: 'Assault near Stadium',
    status: 'new',
    type: 'assault',
    reportedAt: '2024-05-21T11:20:00Z'
  }
];

function DashboardPage() {
  return (
    <div className="stack">
      <section className="grid">
        {mockSummary.map((item) => (
          <SummaryCard key={item.title} {...item} />
        ))}
      </section>
      <section>
        <h2>Recent Incidents</h2>
        <IncidentList incidents={mockIncidents} />
      </section>
    </div>
  );
}

export default DashboardPage;
