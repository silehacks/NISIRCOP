export interface Incident {
  id: number;
  title: string;
  description?: string;
  incidentType: string;
  priority: string;
  latitude: number;
  longitude: number;
  reportedBy: number;
  occurredAt: string;
}
