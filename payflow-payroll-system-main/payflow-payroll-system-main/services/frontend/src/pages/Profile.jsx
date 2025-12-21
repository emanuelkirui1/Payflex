import React, { useEffect, useState } from 'react';

function Profile() {
  const [profile, setProfile] = useState(null);
  const [loading, setLoading] = useState(true);
  const [message, setMessage] = useState(null);

  useEffect(() => {
    fetch('/api/profile/me')
      .then(r => r.json())
      .then(data => { setProfile(data); setLoading(false); })
      .catch(() => { setLoading(false); });
  }, []);

  const save = () => {
    fetch('/api/profile', {
      method: 'PUT', headers: {'Content-Type': 'application/json'}, body: JSON.stringify(profile)
    }).then(r => r.json()).then(data => { setProfile(data); setMessage('Saved'); setTimeout(()=>setMessage(null),2000); })
      .catch(() => setMessage('Save failed'));
  }

  if (loading) return <div>Loading...</div>;
  if (!profile) return <div>Profile not found</div>;

  return (
    <div>
      <h2>My Profile</h2>
      {message && <div style={{color:'green'}}>{message}</div>}
      <div>
        <label>Display name</label>
        <input value={profile.displayName || ''} onChange={e => setProfile({...profile, displayName: e.target.value})} />
      </div>
      <div>
        <label>Phone</label>
        <input value={profile.phone || ''} onChange={e => setProfile({...profile, phone: e.target.value})} />
      </div>
      <div>
        <label>Job title</label>
        <input value={profile.jobTitle || ''} onChange={e => setProfile({...profile, jobTitle: e.target.value})} />
      </div>
      <div>
        <label>Department</label>
        <input value={profile.department || ''} onChange={e => setProfile({...profile, department: e.target.value})} />
      </div>
      <div>
        <label>Bio</label>
        <textarea value={profile.bio || ''} onChange={e => setProfile({...profile, bio: e.target.value})} />
      </div>
      <div>
        <label>Avatar URL</label>
        <input value={profile.avatarUrl || ''} onChange={e => setProfile({...profile, avatarUrl: e.target.value})} />
        {profile.avatarUrl && <div><img src={profile.avatarUrl} alt="avatar" style={{width:80,height:80,borderRadius:40}}/></div>}
      </div>
      <button onClick={save}>Save</button>
    </div>
  );
}

export default Profile;
