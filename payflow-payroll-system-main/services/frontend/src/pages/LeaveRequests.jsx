import React, { useEffect, useState } from 'react';
import api from '../utils/api';

export default function LeaveRequests() {
  const [requests, setRequests] = useState([]);
  const [form, setForm] = useState({ startDate: '', endDate: '', reason: '' });

  useEffect(() => { fetchRequests(); }, []);

  function fetchRequests() {
    api.get('/api/leaves')
      .then(res => setRequests(res.data))
      .catch(err => console.error(err));
  }

  function submit(e) {
    e.preventDefault();
    api.post('/api/leaves', { employeeId: 1, startDate: form.startDate, endDate: form.endDate, reason: form.reason })
      .then(() => { setForm({ startDate: '', endDate: '', reason: '' }); fetchRequests(); })
      .catch(err => console.error(err));
  }

  function approve(id) {
    api.post(`/api/leaves/${id}/approve`, { comment: 'Approved via UI' })
      .then(() => fetchRequests())
      .catch(err => console.error(err));
  }

  function reject(id) {
    api.post(`/api/leaves/${id}/reject`, { comment: 'Rejected via UI' })
      .then(() => fetchRequests())
      .catch(err => console.error(err));
  }

  return (
    <div>
      <h2>Leave Requests</h2>
      <form onSubmit={submit}>
        <label>Start: <input type="date" value={form.startDate} onChange={e=>setForm({...form, startDate: e.target.value})} required /></label>
        <label>End: <input type="date" value={form.endDate} onChange={e=>setForm({...form, endDate: e.target.value})} required /></label>
        <label>Reason: <input value={form.reason} onChange={e=>setForm({...form, reason: e.target.value})} /></label>
        <button type="submit">Request Leave</button>
      </form>

      <h3>Requests</h3>
      <table>
        <thead><tr><th>ID</th><th>Employee</th><th>Start</th><th>End</th><th>Reason</th><th>Status</th><th>Actions</th></tr></thead>
        <tbody>
          {requests.map(r => (
            <tr key={r.id}>
              <td>{r.id}</td>
              <td>{r.employeeId}</td>
              <td>{r.startDate}</td>
              <td>{r.endDate}</td>
              <td>{r.reason}</td>
              <td>{r.status}</td>
              <td>
                {r.status === 'PENDING' && (
                  <>
                    <button onClick={() => approve(r.id)}>Approve</button>
                    <button onClick={() => reject(r.id)}>Reject</button>
                  </>
                )}
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}
