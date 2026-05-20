// ── EC2 ──────────────────────────────────────────────────────────────────────

async function loadEc2() {
    const el = document.getElementById('ec2-content');
    try {
        const data = await fetchJson('/api/instance-info');
        el.innerHTML = `
            <div>
                <div class="flex justify-between border-b border-gray-800 pb-2 mb-2">
                    <span class="text-gray-400">Instance ID</span>
                    <span class="font-mono text-orange-300">${esc(data.instanceId)}</span>
                </div>
                <div class="flex justify-between">
                    <span class="text-gray-400">Region</span>
                    <span class="font-mono text-orange-300">${esc(data.region)}</span>
                </div>
            </div>`;
    } catch (e) {
        setError('ec2-content', e);
    }
}

// ── S3 ───────────────────────────────────────────────────────────────────────

async function loadS3Gallery() {
    const gallery = document.getElementById('s3-gallery');
    try {
        const items = await fetchJson('/api/s3/list');
        if (items.length === 0) {
            gallery.innerHTML = '<p class="text-gray-500 text-sm col-span-3">No images yet. Upload one!</p>';
            return;
        }
        gallery.innerHTML = items.map(item => `
            <div>
                <img src="${esc(item.url)}" alt="${esc(item.key)}"
                     class="w-full h-24 object-cover rounded cursor-pointer hover:opacity-75 transition-opacity"
                     onclick="window.open('${esc(item.url)}', '_blank')"
                     title="${esc(item.key)}">
            </div>`).join('');
    } catch (e) {
        gallery.innerHTML = `<span class="text-red-400 text-sm col-span-3">Error: ${esc(e.message)}</span>`;
    }
}

async function s3Upload() {
    const fileInput = document.getElementById('s3-file');
    const status = document.getElementById('s3-status');
    if (!fileInput.files[0]) { status.textContent = 'Select a file first.'; return; }

    status.textContent = 'Uploading…';
    const form = new FormData();
    form.append('file', fileInput.files[0]);

    try {
        const data = await postForm('/api/s3/upload', form);
        status.textContent = `Uploaded: ${data.key}`;
        fileInput.value = '';
        await loadS3Gallery();
    } catch (e) {
        status.textContent = `Error: ${e.message}`;
    }
}

// ── RDS ──────────────────────────────────────────────────────────────────────

async function loadNotes() {
    const el = document.getElementById('notes-table');
    try {
        const notes = await fetchJson('/api/notes');
        if (notes.length === 0) {
            el.innerHTML = '<p class="text-gray-500 text-sm">No notes yet.</p>';
            return;
        }
        el.innerHTML = `
            <table style="width:100%;font-size:0.8rem;border-collapse:collapse">
                <thead>
                    <tr style="color:#9ca3af;border-bottom:1px solid #1f2937">
                        <th style="text-align:left;padding-bottom:6px;width:2rem">#</th>
                        <th style="text-align:left;padding-bottom:6px">Message</th>
                        <th style="text-align:left;padding-bottom:6px;white-space:nowrap">Created</th>
                    </tr>
                </thead>
                <tbody>
                    ${notes.map(n => `
                    <tr style="border-bottom:1px solid #1f2937">
                        <td style="padding:5px 0;color:#6b7280">${n.id}</td>
                        <td style="padding:5px 0">${esc(n.message)}</td>
                        <td style="padding:5px 0;color:#6b7280;white-space:nowrap">${fmtDate(n.createdAt)}</td>
                    </tr>`).join('')}
                </tbody>
            </table>`;
    } catch (e) {
        el.innerHTML = `<span class="text-red-400 text-sm">Error: ${esc(e.message)}</span>`;
    }
}

async function noteSubmit() {
    const input = document.getElementById('note-input');
    const status = document.getElementById('notes-status');
    const msg = input.value.trim();
    if (!msg) { status.textContent = 'Enter a message first.'; return; }

    status.textContent = 'Saving…';
    try {
        await postJson('/api/notes', { message: msg });
        input.value = '';
        status.textContent = 'Saved!';
        await loadNotes();
    } catch (e) {
        status.textContent = `Error: ${e.message}`;
    }
}

// ── Lambda ───────────────────────────────────────────────────────────────────

async function lambdaDouble() {
    const input = document.getElementById('lambda-input');
    const result = document.getElementById('lambda-result');
    const num = parseInt(input.value, 10);
    if (isNaN(num)) { result.textContent = 'Enter a valid number.'; return; }

    result.innerHTML = '<span style="color:#6b7280">Invoking Lambda…</span>';
    try {
        const data = await postJson('/api/lambda/double', { number: num });
        if (data.error) throw new Error(data.error);
        result.innerHTML = `
            <div style="margin-top:0.5rem;padding:0.75rem;background:#1f2937;border-radius:0.375rem">
                <span style="color:#9ca3af">Input:</span>
                <span style="color:#d8b4fe;font-family:monospace;margin:0 0.25rem">${num}</span>
                <span style="color:#6b7280;margin:0 0.5rem">→</span>
                <span style="color:#9ca3af">Result:</span>
                <span style="color:#d8b4fe;font-family:monospace;font-weight:700;margin-left:0.25rem">${data.result}</span>
            </div>`;
    } catch (e) {
        result.innerHTML = `<span style="color:#f87171">Error: ${esc(e.message)}</span>`;
    }
}

// ── Helpers ──────────────────────────────────────────────────────────────────

async function fetchJson(url) {
    const res = await fetch(url);
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

async function postJson(url, body) {
    const res = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
    });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

async function postForm(url, formData) {
    const res = await fetch(url, { method: 'POST', body: formData });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    return res.json();
}

function setError(id, e) {
    document.getElementById(id).innerHTML = `<span style="color:#f87171">Error: ${esc(e.message)}</span>`;
}

function esc(str) {
    return String(str)
        .replace(/&/g, '&amp;').replace(/</g, '&lt;')
        .replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

function fmtDate(iso) {
    return new Date(iso).toLocaleString(undefined, { dateStyle: 'short', timeStyle: 'short' });
}

// ── Init ─────────────────────────────────────────────────────────────────────

document.addEventListener('DOMContentLoaded', () => {
    loadEc2();
    loadS3Gallery();
    loadNotes();
});
