import React, { useEffect, useState } from 'react';

function formatDate(ts) {
  if (!ts) return '';
  const d = new Date(ts);
  return d.toLocaleString();
}

function generateMockLogs(total = 57) {
  const actions = ['Employee created', 'Payslip generated', 'Salary updated', 'User login', 'Employee deleted'];
  const users = ['alice@example.com', 'bob@example.com', 'finance@example.com', 'admin@example.com'];
  const types = ['Employee', 'Payslip', 'Salary', 'User'];
  const logs = [];
  const now = Date.now();
  for (let i = 0; i < total; i++) {
    const id = i + 1;
    const action = actions[i % actions.length] + (i % 3 === 0 ? `: sample ${i}` : '');
    const user = { email: users[i % users.length] };
    const entityType = types[i % types.length];
    const entityId = 1000 + i;
    const metadata = JSON.stringify({ note: `Sample metadata ${i}`, value: i });
    logs.push({ id, timestamp: new Date(now - i * 60000).toISOString(), user, action, entityType, entityId, metadata });
  }
  return logs;
}

export default function AuditLogs() {
  const [logs, setLogs] = useState([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(20);
  const [totalPages, setTotalPages] = useState(0);
  const [filters, setFilters] = useState({ userId: '', entityType: '', action: '' });
  const [loading, setLoading] = useState(false);
  const [mockMode, setMockMode] = useState(localStorage.getItem('auditMockMode') === 'true');

  useEffect(() => {
    if (mockMode) {
      fetchMockLogs(page, size, filters);
      return;
    }
    const token = localStorage.getItem('token');
    if (!token) return;
    fetchLogs(token, page, size, filters);
  }, [page, size, mockMode]);

  const fetchMockLogs = async (pageParam = 0, sizeParam = 20, filtersParam = filters) => {
    setLoading(true);
    try {
      // Generate a large enough set and apply simple filters & pagination
      let all = generateMockLogs(123);
      if (filtersParam.userId) {
        all = all.filter((l) => String(l.user.email).includes(filtersParam.userId));
      }
      if (filtersParam.entityType) {
        all = all.filter((l) => l.entityType && l.entityType.toLowerCase().includes(filtersParam.entityType.toLowerCase()));
      }
      if (filtersParam.action) {
        all = all.filter((l) => l.action && l.action.toLowerCase().includes(filtersParam.action.toLowerCase()));
      }
      const pages = Math.max(1, Math.ceil(all.length / sizeParam));
      const start = pageParam * sizeParam;
      const pageSlice = all.slice(start, start + sizeParam);
      setLogs(pageSlice);
      setTotalPages(pages);
    } catch (e) {
      console.error('Error generating mock logs', e);
    } finally {
      setLoading(false);
    }
  };

  const fetchLogs = async (token, pageParam = 0, sizeParam = 20, filtersParam = filters) => {
    setLoading(true);
    const params = new URLSearchParams();
    params.set('page', pageParam);
    params.set('size', sizeParam);
    if (filtersParam.userId) params.set('userId', filtersParam.userId);
    if (filtersParam.entityType) params.set('entityType', filtersParam.entityType);
    if (filtersParam.action) params.set('action', filtersParam.action);

    try {
      const resp = await fetch(`http://localhost:8081/api/audit?${params.toString()}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      if (!resp.ok) {
        if (resp.status === 403) {
          alert('You do not have permission to view audit logs');
        } else if (resp.status === 404) {
          alert('Audit API not found');
        }
        setLoading(false);
        return;
      }
      const data = await resp.json();
      setLogs(data.content || []);
      setTotalPages(data.totalPages || 0);
    } catch (e) {
      console.error('Error fetching audits', e);
      alert('Error fetching audit logs');
    } finally {
      setLoading(false);
    }
  };

  const handleFilterChange = (e) => {
    const { name, value } = e.target;
    setFilters({ ...filters, [name]: value });
  };

  const applyFilters = async () => {
    setPage(0);
    if (mockMode) {
      await fetchMockLogs(0, size, filters);
      return;
    }
    const token = localStorage.getItem('token');
    if (!token) return alert('Not authenticated');
    await fetchLogs(token, 0, size, filters);
  };

  const toggleMock = () => {
    const next = !mockMode;
    setMockMode(next);
    localStorage.setItem('auditMockMode', next ? 'true' : 'false');
    setPage(0);
  };

  const prettyMetadata = (metadata) => {
    if (!metadata && metadata !== 0) return '-';
    try {
      const parsed = typeof metadata === 'string' ? JSON.parse(metadata) : metadata;
      return JSON.stringify(parsed, null, 2);
    } catch (e) {
      return String(metadata);
    }
  };

  const exportCurrentPageCSV = () => {
    if (!logs || logs.length === 0) return alert('No logs to export');
    const headers = ['timestamp', 'user_email', 'action', 'entityType', 'entityId', 'metadata'];
    const rows = logs.map((l) => {
      const metadataVal = typeof l.metadata === 'string' ? l.metadata : JSON.stringify(l.metadata);
      return [l.timestamp || '', (l.user && l.user.email) || '', l.action || '', l.entityType || '', l.entityId || '', metadataVal.replace(/\n/g, ' ')]
    });
    const csv = [headers, ...rows].map(r => r.map(cell => `"${String(cell).replace(/"/g, '""')}"`).join(',')).join('\n');
    const blob = new Blob([csv], { type: 'text/csv;charset=utf-8;' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `audit_logs_page_${page+1}.csv`;
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  };


  return (
    <div style={{ padding: '20px' }}>
      <h1>Audit Logs {mockMode && <span style={{ color: '#2a7a2a' }}> (Mock Mode)</span>}</h1>

      <section style={{ marginBottom: '8px' }}>
        <label style={{ display: 'inline-flex', gap: '8px', alignItems: 'center' }}>
          <input type="checkbox" checked={mockMode} onChange={toggleMock} /> Enable mock mode (works without backend)
        </label>
      </section>

      <section style={{ marginBottom: '16px', display: 'flex', gap: '8px', alignItems: 'center' }}>
        <input name="userId" placeholder="User ID" value={filters.userId} onChange={handleFilterChange} />
        <input name="entityType" placeholder="Entity Type" value={filters.entityType} onChange={handleFilterChange} />
        <input name="action" placeholder="Action contains" value={filters.action} onChange={handleFilterChange} />
        <button onClick={applyFilters}>Filter</button>
        <button onClick={() => exportCurrentPageCSV()} style={{ marginLeft: '8px' }}>Export CSV (current page)</button>
      </section>

      <section>
        {loading ? (
          <p>Loading...</p>
        ) : (
          <table style={{ width: '100%', borderCollapse: 'collapse' }}>
            <thead>
              <tr>
                <th style={{ border: '1px solid #ddd', padding: '8px' }}>Timestamp</th>
                <th style={{ border: '1px solid #ddd', padding: '8px' }}>User</th>
                <th style={{ border: '1px solid #ddd', padding: '8px' }}>Action</th>
                <th style={{ border: '1px solid #ddd', padding: '8px' }}>Entity</th>
                <th style={{ border: '1px solid #ddd', padding: '8px' }}>Entity ID</th>
                <th style={{ border: '1px solid #ddd', padding: '8px' }}>Metadata</th>
                <th style={{ border: '1px solid #ddd', padding: '8px' }}>View</th>
              </tr>
            </thead>
            <tbody>
              {logs.map((l) => (
                <React.Fragment key={l.id}>
                  <tr>
                    <td style={{ border: '1px solid #ddd', padding: '8px' }}>{formatDate(l.timestamp)}</td>
                    <td style={{ border: '1px solid #ddd', padding: '8px' }}>{l.user ? l.user.email : '-'}</td>
                    <td style={{ border: '1px solid #ddd', padding: '8px' }}>{l.action}</td>
                    <td style={{ border: '1px solid #ddd', padding: '8px' }}>{l.entityType || '-'}</td>
                    <td style={{ border: '1px solid #ddd', padding: '8px' }}>{l.entityId || '-'}</td>
                    <td style={{ border: '1px solid #ddd', padding: '8px' }}>{typeof l.metadata === 'string' ? (l.metadata.length > 60 ? l.metadata.slice(0, 60)+'...' : l.metadata) : JSON.stringify(l.metadata)}</td>
                    <td style={{ border: '1px solid #ddd', padding: '8px' }}>
                      <details>
                        <summary style={{ cursor: 'pointer' }}>View metadata</summary>
                        <pre style={{ whiteSpace: 'pre-wrap', maxHeight: '300px', overflow: 'auto', background: '#f9f9f9', padding: '8px' }}>{prettyMetadata(l.metadata)}</pre>
                      </details>
                    </td>
                  </tr>
                </React.Fragment>
              ))}
              {logs.length === 0 && (
                <tr>
                  <td colSpan="7" style={{ padding: '12px', textAlign: 'center' }}>No audit logs found</td>
                </tr>
              )}
            </tbody>
          </table>
        )}
      </section>

      <section style={{ marginTop: '12px', display: 'flex', gap: '8px', alignItems: 'center' }}>
        <button onClick={() => setPage((p) => Math.max(0, p - 1))} disabled={page <= 0}>Prev</button>
        <span>Page {page + 1} / {Math.max(1, totalPages)}</span>
        <button onClick={() => setPage((p) => (p + 1 < totalPages ? p + 1 : p))} disabled={page + 1 >= totalPages}>Next</button>
        <select value={size} onChange={(e) => setSize(Number(e.target.value))}>
          <option value={10}>10</option>
          <option value={20}>20</option>
          <option value={50}>50</option>
        </select>
      </section>
    </div>
  );
}
