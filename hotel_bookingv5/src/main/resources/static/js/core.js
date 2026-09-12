(function(){
  "use strict";
  var LS = {
    get:function(k,d){try{var v=localStorage.getItem(k);return v===null?d:JSON.parse(v);}catch(e){return d;}},
    set:function(k,v){try{localStorage.setItem(k,JSON.stringify(v));}catch(e){}},
    remove:function(k){try{localStorage.removeItem(k);}catch(e){}}
  };

  var SVGS = {
    home:'<path d="M3 9.5 12 3l9 6.5V21a1 1 0 0 1-1 1h-5v-7h-6v7H4a1 1 0 0 1-1-1z"/>',
    layout:'<rect x="3" y="3" width="18" height="7" rx="1.5"/><rect x="3" y="14" width="9" height="7" rx="1.5"/><rect x="16" y="14" width="5" height="7" rx="1.5"/>',
    bed:'<path d="M2 8v13M2 17h20v4M2 8a4 4 0 0 0 4-4h12a3 3 0 0 1-3 3H9a2 2 0 0 0-2-2 2 2 0 0 0-3 3zM2 21h20"/>',
    key:'<path d="M15.5 8A4.5 4.5 0 1 0 8 8a4.5 4.5 0 0 0 7.5 0zM21 21l-7-7M15.5 8 21 7.5"/>',
    calendar:'<rect x="3" y="4" width="18" height="17" rx="2"/><path d="M8 2v4M16 2v4M3 9h18M8 13h.01M12 13h.01M16 13h.01M8 17h.01M12 17h.01"/>',
    clock:'<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
    users:'<path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M22 21v-2a4 4 0 0 0-3-3.87M16 3.13a4 4 0 0 1 0 7.75"/>',
    user:'<path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"/><circle cx="12" cy="7" r="4"/>',
    heart:'<path d="M20.8 4.6a5.5 5.5 0 0 0-7.8 0L12 5.7l-1-1.1a5.5 5.5 0 0 0-7.8 7.8l1 1.1L12 21l7.8-7.5 1-1.1a5.5 5.5 0 0 0 0-7.8z"/>',
    star:'<path d="m12 2 3.1 6.3 6.9 1-5 4.9 1.2 6.8L12 17.8 5.8 21l1.2-6.8-5-4.9 6.9-1z"/>',
    search:'<circle cx="11" cy="11" r="7"/><path d="m21 21-4.3-4.3"/>',
    wifi:'<path d="M5 12.5a10 10 0 0 1 14 0M8.5 15.5a5 5 0 0 1 7 0M12 18.5h.01"/><path d="M2 8.8a16 16 0 0 1 20 0"/>',
    card:'<rect x="2" y="5" width="20" height="14" rx="2"/><path d="M2 10h20M6 15h4"/>',
    wallet:'<path d="M21 12V7H5a2 2 0 0 1 0-4h14v4"/><path d="M3 5v14a2 2 0 0 0 2 2h16v-5"/><path d="M18 12a2 2 0 0 0 0 4h4v-4z"/>',
    bookmark:'<path d="M19 21l-7-5-7 5V5a2 2 0 0 1 2-2h10a2 2 0 0 1 2 2z"/>',
    settings:'<circle cx="12" cy="12" r="3"/><path d="M19.4 15a1.7 1.7 0 0 0 .3 1.9l.1.1a2 2 0 1 1-2.8 2.8l-.1-.1a1.7 1.7 0 0 0-1.9-.3 1.7 1.7 0 0 0-1 1.5V21a2 2 0 1 1-4 0v-.1a1.7 1.7 0 0 0-1-1.5 1.7 1.7 0 0 0-1.9.3l-.1.1a2 2 0 1 1-2.8-2.8l.1-.1a1.7 1.7 0 0 0 .3-1.9 1.7 1.7 0 0 0-1.5-1H3a2 2 0 1 1 0-4h.1a1.7 1.7 0 0 0 1.5-1 1.7 1.7 0 0 0-.3-1.9l-.1-.1a2 2 0 1 1 2.8-2.8l.1.1a1.7 1.7 0 0 0 1.9.3h.1a1.7 1.7 0 0 0 1-1.5V3a2 2 0 1 1 4 0v.1a1.7 1.7 0 0 0 1 1.5h.1a1.7 1.7 0 0 0 1.9-.3l.1-.1a2 2 0 1 1 2.8 2.8l-.1.1a1.7 1.7 0 0 0-.3 1.9v.1a1.7 1.7 0 0 0 1.5 1H21a2 2 0 1 1 0 4h-.1a1.7 1.7 0 0 0-1.5 1z"/>',
    shield:'<path d="M12 22s8-3.6 8-10V5l-8-3-8 3v7c0 6.4 8 10 8 10z"/>',
    bell:'<path d="M18 8a6 6 0 0 0-12 0c0 7-3 9-3 9h18s-3-2-3-9M13.7 21a2 2 0 0 1-3.4 0"/>',
    menu:'<path d="M3 6h18M3 12h18M3 18h18"/>',
    x:'<path d="M18 6 6 18M6 6l12 12"/>',
    check:'<path d="M20 6 9 17l-5-5"/>',
    checkCircle:'<path d="M22 11.1V12a10 10 0 1 1-5.9-9.1"/><path d="M22 4 12 14l-3-3"/>',
    info:'<circle cx="12" cy="12" r="9"/><path d="M12 16v-4M12 8h.01"/>',
    alert:'<path d="M10.3 3.8 1.8 18a2 2 0 0 0 1.7 3h17a2 2 0 0 0 1.7-3L13.7 3.8a2 2 0 0 0-3.4 0z"/><path d="M12 9v4M12 17h.01"/>',
    alertCircle:'<circle cx="12" cy="12" r="9"/><path d="M12 8v4M12 16h.01"/>',
    mail:'<rect x="2" y="4" width="20" height="16" rx="2"/><path d="m22 7-10 6L2 7"/>',
    phone:'<path d="M22 16.9v3a2 2 0 0 1-2.2 2 19.8 19.8 0 0 1-8.6-3 19.5 19.5 0 0 1-6-6 19.8 19.8 0 0 1-3-8.6A2 2 0 0 1 4.1 2h3a2 2 0 0 1 2 1.7c.1 1 .4 2 .7 2.9a2 2 0 0 1-.5 2.1L8.1 10a16 16 0 0 0 6 6l1.3-1.3a2 2 0 0 1 2.1-.5c.9.3 1.9.6 2.9.7a2 2 0 0 1 1.6 2z"/>',
    pin:'<path d="M20 10c0 6-8 12-8 12S4 16 4 10a8 8 0 0 1 16 0z"/><circle cx="12" cy="10" r="3"/>',
    chevDown:'<path d="m6 9 6 6 6-6"/>',
    chevLeft:'<path d="m15 18-6-6 6-6"/>',
    chevRight:'<path d="m9 18 6-6-6-6"/>',
    logout:'<path d="M9 21H5a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h4M16 17l5-5-5-5M21 12H9"/>',
    edit:'<path d="M12 20h9"/><path d="M16.5 3.5a2.1 2.1 0 0 1 3 3L7 19l-4 1 1-4z"/>',
    trash:'<path d="M3 6h18M8 6V4a2 2 0 0 1 2-2h4a2 2 0 0 1 2 2v2M19 6v14a2 2 0 0 1-2 2H7a2 2 0 0 1-2-2V6M10 11v6M14 11v6"/>',
    plus:'<path d="M12 5v14M5 12h14"/>',
    minus:'<path d="M5 12h14"/>',
    filter:'<path d="M22 3H2l8 9.5V19l4 2v-8.5z"/>',
    arrowRight:'<path d="M5 12h14M12 5l7 7-7 7"/>',
    arrowLeft:'<path d="M19 12H5M12 19l-7-7 7-7"/>',
    eye:'<path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/>',
    eyeOff:'<path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.5 18.5 0 0 1 5.06-5.94M9.9 4.24A9.1 9.1 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19M14.12 14.12a3 3 0 1 1-4.24-4.24"/><path d="m1 1 22 22"/>',
    camera:'<path d="M23 19a2 2 0 0 1-2 2H3a2 2 0 0 1-2-2V8a2 2 0 0 1 2-2h4l2-3h6l2 3h4a2 2 0 0 1 2 2z"/><circle cx="12" cy="13" r="4"/>',
    image:'<rect x="3" y="3" width="18" height="18" rx="2"/><circle cx="8.5" cy="8.5" r="1.5"/><path d="m21 15-5-5L5 21"/>',
    download:'<path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4M7 10l5 5 5-5M12 15V3"/>',
    printer:'<path d="M6 9V2h12v7M6 18H4a2 2 0 0 1-2-2v-5a2 2 0 0 1 2-2h16a2 2 0 0 1 2 2v5a2 2 0 0 1-2 2h-2"/><path d="M6 14h12v8H6z"/>',
    share:'<circle cx="18" cy="5" r="3"/><circle cx="6" cy="12" r="3"/><circle cx="18" cy="19" r="3"/><path d="m8.6 13.5 6.8 4M15.4 6.5l-6.8 4"/>',
    more:'<circle cx="12" cy="5" r="1"/><circle cx="12" cy="12" r="1"/><circle cx="12" cy="19" r="1"/>',
    building:'<rect x="4" y="2" width="16" height="20" rx="2"/><path d="M9 22v-4h6v4M8 6h.01M16 6h.01M12 6h.01M8 10h.01M16 10h.01M12 10h.01M8 14h.01M16 14h.01M12 14h.01"/>',
    door:'<path d="M3 21h18M6 21V4a1 1 0 0 1 1-1h10a1 1 0 0 1 1 1v17M13 21v-3h2v3"/><circle cx="13" cy="10" r="1"/>',
    gift:'<rect x="3" y="8" width="18" height="4" rx="1"/><path d="M12 8v13M5 12v9h14v-9M19 8c1 0 2-.8 2-2s-1-2-2-2c-3 0-3 4-7 4s-4-4-7-4c-1 0-2 .8-2 2s1 2 2 2"/>',
    tag:'<path d="M20.6 13.4 12 22l-9-9V4a1 1 0 0 1 1-1h9z"/><circle cx="7.5" cy="7.5" r="1.5"/>',
    percent:'<path d="M19 5 5 19"/><circle cx="6.5" cy="6.5" r="2.5"/><circle cx="17.5" cy="17.5" r="2.5"/>',
    trend:'<path d="m22 7-8.5 8.5-5-5L2 17"/><path d="M16 7h6v6"/>',
    pie:'<path d="M21.2 15.9A10 10 0 1 1 8 2.8"/><path d="M22 12A10 10 0 0 0 12 2v10z"/>',
    bar:'<path d="M3 3v18h18"/><path d="M7 15v-4M12 15V8M17 15v-6"/>',
    activity:'<path d="M22 12h-4l-3 9L9 3l-3 9H2"/>',
    utensils:'<path d="M3 2v7a2 2 0 0 0 2 2h2v11M6 2v20M17 2c-2 1-3 4-2 6s3 2 4 4v8M17 2c1 1 1 4 1 6"/>',
    coffee:'<path d="M17 8h1a4 4 0 1 1 0 8h-1M3 8h14v9a4 4 0 0 1-4 4H7a4 4 0 0 1-4-4zM6 2v2M10 2v2M14 2v2"/>',
    car:'<path d="M5 16H3v-6l2-5h14l2 5v6h-2"/><circle cx="7" cy="16" r="2.5"/><circle cx="17" cy="16" r="2.5"/><path d="M9 16h6M5 10h14"/>',
    moon:'<path d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8z"/>',
    sun:'<circle cx="12" cy="12" r="4"/><path d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"/>',
    refresh:'<path d="M23 4v6h-6M1 20v-6h6"/><path d="M3.5 9a9 9 0 0 1 14.9-3.4L23 10M1 14l4.6 4.4A9 9 0 0 0 20.5 15"/>',
    ban:'<circle cx="12" cy="12" r="9"/><path d="M5.6 5.6l12.8 12.8"/>',
    lock:'<rect x="3" y="11" width="18" height="10" rx="2"/><path d="M7 11V7a5 5 0 0 1 10 0v4"/>',
    copy:'<rect x="9" y="9" width="12" height="12" rx="2"/><path d="M5 15H4a2 2 0 0 1-2-2V4a2 2 0 0 1 2-2h9a2 2 0 0 1 2 2v1"/>',
    dollar:'<path d="M12 1v22M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/>',
    qr:'<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><path d="M14 14h3v3h-3zM20 20h1M14 20h2M20 17.5V14"/>',
    grid2:'<rect x="3" y="3" width="7" height="7" rx="1"/><rect x="14" y="3" width="7" height="7" rx="1"/><rect x="3" y="14" width="7" height="7" rx="1"/><rect x="14" y="14" width="7" height="7" rx="1"/>',
    feather:'<path d="M20.2 2.8a6 6 0 0 0-8.5 0L5 9.5V19h9.5l6.7-6.7a6 6 0 0 0-1-9.5z"/><path d="M16 8 2 22M17.5 15H9"/>',
    zap:'<path d="M13 2 3 14h9l-1 8 10-12h-9z"/>',
    crop:'<path d="M6 2v14a2 2 0 0 0 2 2h14M18 22V8a2 2 0 0 0-2-2H2"/>',
    clock2:'<circle cx="12" cy="12" r="9"/><path d="M12 7v5l3 2"/>',
    landmark:'<path d="M3 22h18M6 18v-7M10 18v-7M14 18v-7M18 18v-7M12 2 3 7h18z"/>',
    inbox:'<path d="M22 12h-6l-2 3h-4l-2-3H2"/><path d="M5.5 5.1 2 12v6a2 2 0 0 0 2 2h16a2 2 0 0 0 2-2v-6l-3.5-6.9a2 2 0 0 0-1.8-1.1H7.3a2 2 0 0 0-1.8 1.1z"/>',
    map:'<path d="M1 6v16l7-4 8 4 7-4V2l-7 4-8-4z"/><path d="M8 2v16M16 6v16"/>',
    briefcase:'<rect x="2" y="7" width="20" height="14" rx="2"/><path d="M16 21V5a2 2 0 0 0-2-2h-4a2 2 0 0 0-2 2v16"/>',
    bank:'<path d="M3 21h18M4 10h16M5 6 12 2l7 4M6 10v8M10 10v8M14 10v8M18 10v8"/><path d="M12 14h.01"/>',
    cash:'<rect x="2" y="6" width="20" height="12" rx="2"/><circle cx="12" cy="12" r="2.5"/><path d="M6 12h.01M18 12h.01"/>',
    droplet:'<path d="M12 2.7S6 9.6 6 13.8a6 6 0 0 0 12 0C18 9.6 12 2.7 12 2.7z"/>',
    sparkles:'<path d="M12 3l1.9 5.1L19 10l-5.1 1.9L12 17l-1.9-5.1L5 10l5.1-1.9zM19 15l.8 2.2L22 18l-2.2.8L19 21l-.8-2.2L16 18l2.2-.8zM5 2l.6 1.4L7 4l-1.4.6L5 6l-.6-1.4L3 4l1.4-.6z"/>',
    globe:'<circle cx="12" cy="12" r="9"/><path d="M3 12h18M12 3a15 15 0 0 1 0 18M12 3a15 15 0 0 0 0 18"/>',
    servers:'<rect x="2" y="3" width="20" height="7" rx="2"/><rect x="2" y="14" width="20" height="7" rx="2"/><path d="M6 6.5h.01M6 17.5h.01M10 6.5h.01M10 17.5h.01"/>',
    power:'<path d="M12 2v10"/><path d="M18.4 6.6a9 9 0 1 1-12.8 0"/>'
  };

  function icon(name, cls){
    cls = cls || "icon";
    return '<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.8" stroke-linecap="round" stroke-linejoin="round" class="'+cls+'" aria-hidden="true">'+(SVGS[name]||SVGS.info)+'</svg>';
  }
  function stars(r){
    r = Math.round((r||0)*2)/2;
    var full=Math.floor(r), half=(r-full)>=0.4?1:0;
    var s='';
    for(var i=0;i<5;i++){
      if(i<full) s+='★';
      else if(i===full&&half) s+='<span style="font-size:.75em">★</span>';
      else s+='☆';
    }
    return s;
  }
  function esc(s){
    return String(s==null?'':s).replace(/[&<>"']/g,function(c){return {'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;'}[c];});
  }
  function cap(s){ return s.charAt(0).toUpperCase()+s.slice(1); }

  var IMG = function(main,w){ return 'https://images.unsplash.com/'+main+((w||400)?('?auto=format&fit=crop&w='+(w||600)+'&q=68'):''); };
  var PHOTOS = {
    exterior:'photo-1566073771259-6a8506099945',
    exteriorNight:'photo-1551882547-ff40c63fe5fa',
    lobby:'photo-1564501049412-61c2a3083791',
    pool:'photo-1571896349842-33c89424de2d',
    pool2:'photo-1571003123894-1f0594d2b5d9',
    restaurant:'photo-1414235077428-338989a2e8c0',
    gym:'photo-1534438327276-14e5300c3a48',
    spa:'photo-1544161515-4ab6ce6db874',
    rooftop:'photo-1470770841072-f978cf4d019e',
    bed1:'photo-1590490360182-c33d57733427',
    bed2:'photo-1611892440504-42a792e24d32',
    bed3:'photo-1618773928121-c32242e63f39',
    bed4:'photo-1566073771259-6a8506099945',
    bed5:'photo-1582719478250-c89cae4dc85b',
    bed6:'photo-1631049307264-da0ec9d70304',
    bed7:'photo-1591088398332-8a7791972843',
    suite:'photo-1584132967334-10e028bd69f7',
    breakfast:'photo-1533089860892-a7c6f0a88666',
    coffee:'photo-1509042239860-f550ce710b93',
    food1:'photo-1546069901-ba9599a7e63c',
    food2:'photo-1565299624946-b28f40a0ae38',
    food3:'photo-1555939594-58d7cb561ad1',
    food4:'photo-1512621776951-a57141f2eefd',
    food5:'photo-1540189549336-e6e99c3679fe',
    fruit:'photo-1490474418585-ba9bad8fd0ea',
    bar:'photo-1514362545857-3bc16c4c7d1b',
    drink:'photo-1544145945-f90425340c7e',
    towel:'photo-1584622650111-993a426fbf0a',
    av:'photo-1552346154-21d32810aba3',
    avatar:'photo-1507003211169-0a1dd7228f2d',
    avatarF:'photo-1438761681033-6461ffad8d80'
  };
  function ph(k,w){ return IMG(PHOTOS[k],w||700); }

  function addDays(d,n){ var x=new Date(d); x.setDate(x.getDate()+n); return x; }
  function fmtDate(d){ var o={year:'numeric',month:'short',day:'numeric'}; return new Date(d).toLocaleDateString('en-US',o); }
  function fmtDateLong(d){ var o={year:'numeric',month:'long',day:'numeric'}; return new Date(d).toLocaleDateString('en-US',o); }
  function fmtWhen(d){ var o={year:'numeric',month:'short',day:'numeric'}; var x=new Date(d); var today=new Date(); today.setHours(0,0,0,0); x.setHours(0,0,0,0); var diff=Math.round((x-today)/86400000); if(diff===0)return 'Today'; if(diff===1)return 'Tomorrow'; if(diff===-1)return 'Yesterday'; if(diff>1&&diff<7)return x.toLocaleDateString('en-US',o)+' ('+diff+' days)'; return x.toLocaleDateString('en-US',o); }
  function nights(a,b){ if(!a||!b)return 0; var x=Math.round((new Date(b).setHours(0,0,0,0)-new Date(a).setHours(0,0,0,0))/86400000); return x>0?x:0; }
  function fmtDateISO(d){ var x=new Date(d); return x.getFullYear()+'-'+String(x.getMonth()+1).padStart(2,'0')+'-'+String(x.getDate()).padStart(2,'0'); }
  function parseISO(s){ if(!s)return new Date(); var p=s.split('-'); return new Date(+p[0],+p[1]-1,+p[2]); }
  function todayISO(){ return fmtDateISO(new Date()); }
  function today(){ var d=new Date(); d.setHours(0,0,0,0); return d; }
  function money(n){ return '$'+Number(n||0).toLocaleString('en-US',{maximumFractionDigits:0}); }
  function initials(n){ n=n||'G'; var p=n.trim().split(/\s+/); return ((p[0]||'')[0]||'G')+(p.length>1?(p[p.length-1][0]||''):''); }
  function avatarClass(role){ return role==='SUPER_ADMIN'?'avatar super':(role==='ADMIN'?'avatar admin':(role==='STAFF'?'avatar staff':'avatar user')); }
  var AVATAR_CACHE={};
  function avatarHTML(u,cls){
    cls=cls||'avatar';
    var role=u&&u.role?u.role:'GUEST';
    if(u&&u.image){
      return '<span class="'+cls+' '+role.toLowerCase().replace('_','-')+'"><img src="'+esc(u.image)+'" alt=""></span>';
    }
    var c=AVATAR_CACHE[cls]||(AVATAR_CACHE[cls]={});
    var col=c[role]||'';
    var seed=(u?u.id||u.email||u.name:'')||'';
    var names=['#9c6a1e','#0d524d','#14506e','#7b2c6d','#a14a24','#2e6b3a','#5a2d82','#33506b'];
    var h=0; for(var i=0;i<String(seed).length;i++){h=(h*31+String(seed).charCodeAt(i))%997;}
    var bg=names[h%names.length];
    var fs=cls.indexOf('lg')>-1?1.4:(cls.indexOf('sm')>-1?.7:(cls.indexOf('xl')>-1?2:.85));
    return '<span class="'+cls+'" style="background:linear-gradient(135deg,'+bg+',#2b5960)">'+esc(initials(u&&u.name))+'</span>';
  }

  var TAX_RATE = 0.1;
  var ROOM_STATUS = ['available','occupied','reserved','maintenance','cleaning'];
  var AMENITIES = ['WiFi','Air Conditioning','TV','Breakfast','Parking','Pool','Gym','Spa','Balcony','Room Service'];

  /* ---------- seed data ---------- */
  function seedRooms(){
    var m=function(n){return {
      wifi:'bed1', double:'bed3', family:'bed4', suite:'suite', premium:'bed6', deluxe:'bed2', twin:'bed7', single:'bed5'
    }[n]||'bed1';};
    function room(id,num,name,type,price,discount,capacity,beds,size,floor,view,rating,reviews,feat,pet,amen,status){
      var img=m(type);
      return {id:id,number:num,name:name,type:type,desc:'Comfort and refinement come together in the '+name+'. Thoughtful amenities, crisp linens and a serene palette make every stay effortless, while a dedicated team is always a call away to personalise your visit.',
        price:price,discount:discount||0,capacity:capacity,beds:beds,size:size,floor:floor,view:view,rating:rating||4.6,reviews:reviews||120,
        featured:!!feat,petFriendly:!!pet,amenities:amen||['WiFi','Air Conditioning','TV','Breakfast'],
        images:[ph(img,900),ph(img,700),ph(img,600),ph('bed4',700),ph('bed6',700)],
        status:status||'available',popularity:reviews||100,createdAt:Date.now()-reviews*86400000};
    }
    return [
      room('r1',104,'Deluxe King Room','Deluxe',150,20,2,'1 King Bed',38,2,'Garden View',4.8,214,true,false,['WiFi','Air Conditioning','TV','Breakfast','Balcony','Room Service']),
      room('r2',501,'Premium Ocean Suite','Suite',320,15,3,'1 King + Sofa Bed',62,5,'Ocean View',4.9,138,true,false,['WiFi','Air Conditioning','TV','Breakfast','Pool','Spa','Balcony','Room Service']),
      room('r3',312,'Family Terrace Room','Family',190,0,5,'2 Queen Beds',52,3,'Courtyard View',4.7,176,true,true,['WiFi','Air Conditioning','TV','Breakfast','Balcony','Room Service']),
      room('r4',108,'Classic Double Room','Double',110,10,2,'1 Queen Bed',30,1,'City View',4.5,240,false,false,['WiFi','Air Conditioning','TV','Breakfast','Parking']),
      room('r5',112,'Standard Twin Room','Twin',105,0,2,'2 Single Beds',29,1,'City View',4.4,152,false,false,['WiFi','Air Conditioning','TV','Breakfast']),
      room('r6',101,'Cozy Single Room','Single',85,5,1,'1 Single Bed',22,1,'Garden View',4.3,98,false,false,['WiFi','Air Conditioning','TV','Breakfast']),
      room('r7',207,'Deluxe Twin Room','Deluxe',165,0,3,'2 Double Beds',41,2,'Pool View',4.7,167,false,false,['WiFi','Air Conditioning','TV','Breakfast','Pool','Room Service']),
      room('r8',408,'Premium Corner View','Premium',260,25,3,'1 King + Sofa Bed',55,4,'City Panorama',4.9,121,false,false,['WiFi','Air Conditioning','TV','Breakfast','Gym','Spa','Balcony','Room Service']),
      room('r9',404,'Executive Suite','Suite',285,0,3,'1 King Bed',58,4,'Bay View',4.8,204,false,false,['WiFi','Air Conditioning','TV','Breakfast','Gym','Spa','Room Service']),
      room('r10',218,'Garden Family Suite','Family',210,0,6,'3 Double Beds',66,2,'Garden View',4.6,89,false,true,['WiFi','Air Conditioning','TV','Breakfast','Pool','Balcony','Room Service']),
      room('r11',206,'Double Poolside Room','Double',125,0,2,'1 Queen Bed',32,2,'Pool View',4.6,133,false,false,['WiFi','Air Conditioning','TV','Breakfast','Pool','Room Service']),
      room('r12',601,'Grand Penthouse Suite','Suite',450,0,4,'King + Twin Beds',90,6,'Rooftop Skyline',5.0,74,true,false,['WiFi','Air Conditioning','TV','Breakfast','Pool','Gym','Spa','Balcony','Room Service'])
    ];
  }
  var DEMO_USERS=[
    {id:'u1',name:'Fazley Rabbi',email:'user@hotel.com',password:'User123',phone:'+880 1712 445566',role:'GUEST',status:'active',address:'12 Gulshan Avenue, Dhaka',memberSince:addDays(new Date(),-390),image:'',bookings:0},
    {id:'u2',name:'Ayesha Karim',email:'admin@hotel.com',password:'Admin123',phone:'+880 1711 889900',role:'ADMIN',status:'active',address:'Hotel Residence Block, Dhaka',memberSince:addDays(new Date(),-540),image:'',bookings:0},
    {id:'u3',name:'Samiul Haque',email:'superadmin@hotel.com',password:'Super123',phone:'+880 1712 665544',role:'SUPER_ADMIN',status:'active',address:'Level 6, Grand Meridian, Cox\u2019s Bazar',memberSince:addDays(new Date(),-800),image:'',bookings:0},
    {id:'u4',name:'Sarah Mitchell',email:'sarah@example.com',password:'Sarah123',phone:'+1 415 882 1004',role:'GUEST',status:'active',address:'88 Market Street, San Francisco',memberSince:addDays(new Date(),-300),image:'',bookings:0},
    {id:'u5',name:'James Chen',email:'james@example.com',password:'James123',phone:'+65 9123 4477',role:'GUEST',status:'active',address:'9 Raffles Place, Singapore',memberSince:addDays(new Date(),-210),image:'',bookings:0},
    {id:'u6',name:'Emily Carter',email:'emily@example.com',password:'Emily123',phone:'+44 7700 900321',role:'GUEST',status:'inactive',address:'21 Baker Street, London',memberSince:addDays(new Date(),-520),image:'',bookings:0},
    {id:'u7',name:'David Wilson',email:'david@example.com',password:'David123',phone:'+61 401 223344',role:'GUEST',status:'active',address:'5 Harbour Boulevard, Sydney',memberSince:addDays(new Date(),-140),image:'',bookings:0},
    {id:'u8',name:'Aisha Rahman',email:'aisha@example.com',password:'Aisha123',phone:'+880 1812 998877',role:'GUEST',status:'active',address:'House 17, Road 7, Banani, Dhaka',memberSince:addDays(new Date(),-95),image:'',bookings:0},
    {id:'u9',name:'Rahim Chowdhury',email:'staff@hotel.com',password:'Staff123',phone:'+880 1813 556677',role:'STAFF',status:'active',address:'Front Office Block, Grand Meridian',memberSince:addDays(new Date(),-200),image:'',bookings:0}
  ];
  function seedUsers(){
    return DEMO_USERS.slice();
  }
  function getDemoUsers(){ return DEMO_USERS.filter(function(u){return ['user@hotel.com','admin@hotel.com','superadmin@hotel.com','staff@hotel.com'].indexOf(u.email)>-1; }); }
  function seedBookings(){
    var t=new Date(); t.setHours(0,0,0,0);
    var defs=[
      {id:'b1',user:'u1',room:'r1',cin:5,cout:9,adults:2,children:0,pets:false,status:'confirmed',pay:'unpaid',svc:['breakfast'],promo:'SUMMER25',svcTotal:72},
      {id:'b2',user:'u1',room:'r2',cin:0,cout:2,adults:2,children:1,pets:false,status:'active',pay:'paid',svc:['spa','breakfast'],svcTotal:98},
      {id:'b3',user:'u1',room:'r7',cin:-30,cout:-28,adults:2,children:0,pets:false,status:'completed',pay:'paid',svc:[],promo:'WELCOME10',svcTotal:0},
      {id:'b4',user:'u1',room:'r3',cin:-60,cout:-55,adults:4,children:1,pets:true,status:'completed',pay:'paid',svc:['laundry'],svcTotal:24},
      {id:'b5',user:'u1',room:'r8',cin:14,cout:17,adults:2,children:0,pets:false,status:'pending',pay:'unpaid',svc:['airport'],svcTotal:35},
      {id:'b6',user:'u1',room:'r4',cin:-15,cout:-13,adults:1,children:0,pets:false,status:'cancelled',pay:'unpaid',svc:[],svcTotal:0},
      {id:'b7',user:'u4',room:'r9',cin:2,cout:6,adults:2,children:0,pets:false,status:'confirmed',pay:'paid',svc:['breakfast'],svcTotal:90},
      {id:'b8',user:'u4',room:'r5',cin:-40,cout:-38,adults:2,children:0,pets:false,status:'completed',pay:'paid',svc:[],svcTotal:0},
      {id:'b9',user:'u5',room:'r12',cin:0,cout:3,adults:3,children:1,pets:false,status:'active',pay:'paid',svc:['airport','breakfast'],svcTotal:70},
      {id:'b10',user:'u5',room:'r2',cin:-20,cout:-16,adults:2,children:0,pets:false,status:'completed',pay:'paid',svc:['spa','breakfast'],svcTotal:105},
      {id:'b11',user:'u7',room:'r3',cin:-5,cout:-2,adults:3,children:2,pets:false,status:'completed',pay:'paid',svc:['laundry','breakfast'],svcTotal:48},
      {id:'b12',user:'u7',room:'r11',cin:9,cout:13,adults:2,children:0,pets:false,status:'confirmed',pay:'unpaid',svc:[],svcTotal:0},
      {id:'b13',user:'u8',room:'r6',cin:-90,cout:-88,adults:1,children:0,pets:false,status:'completed',pay:'paid',svc:[],svcTotal:0},
      {id:'b14',user:'u8',room:'r1',cin:1,cout:4,adults:2,children:1,pets:false,status:'confirmed',pay:'unpaid',svc:['breakfast'],svcTotal:48},
      {id:'b15',user:'u4',room:'r10',cin:20,cout:25,adults:4,children:2,pets:true,status:'pending',pay:'unpaid',svc:['extraBed','breakfast'],svcTotal:125},
      {id:'b16',user:'u6',room:'r8',cin:-3,cout:2,adults:2,children:0,pets:false,status:'active',pay:'unpaid',svc:[],svcTotal:0}
    ];
    return defs.map(function(d,i){
      var r=null,u=null;
      var seed=LS.get('hv_seedbase',null)||{rooms:[],users:[],bookings:[],promotions:[],promoCodes:[],food:[],gallery:[],notifs:[]};
      if(seed.rooms.length){r=seed.rooms.filter(function(x){return x.id===d.room;})[0];}
      if(seed.users.length){u=seed.users.filter(function(x){return x.id===d.user;})[0];}
      if(!r)r={price:150,discount:0,name:'Deluxe King Room',type:'Deluxe',images:[ph('bed1',500)]};
      if(!u)u={name:'Guest'};
      var cin=addDays(t,d.cin), cout=addDays(t,d.cout), n=nights(cin,cout);
      var base=r.price*n, disc=Math.round(base*((r.discount||0)/100));
      var svcT=d.svcTotal||0;
      var tax=Math.round((base-disc+svcT)*TAX_RATE);
      var total=base-disc+svcT+tax;
      return {id:d.id,bookingId:bookIdSeq(d.id),guest:u.name,userId:d.user,roomId:d.room,roomName:r.name,roomType:r.type,roomImage:r.images[0],
        checkIn:fmtDateISO(cin),checkOut:fmtDateISO(cout),adults:d.adults,children:d.children,pets:d.pets,
        roomPrice:r.price,nightlyDisc:r.discount||0,nights:n,totalAmount:total,discountAmount:disc,servicesTotal:svcT,taxAmount:tax,
        payment:d.pay,status:d.status,services:d.svc||[],promoCode:d.promo||null,createdAt:fmtDateISO(addDays(cin,-2))};
    });
  }
  function bookIdSeq(id){
    var m={'b1':1,'b7':7,'b14':14,'b9':9,'b12':12,'b5':5,'b15':15,'b2':2,'b16':16,'b3':3,'b8':8,'b10':10,'b11':11,'b13':13,'b6':6,'b4':4};
    var n=m[id]||Math.floor(Math.random()*9000)+100;
    var y=new Date().getFullYear();
    return 'HB-'+y+'-'+String(1000+n).slice(-4)+((id in m)?'':'');
  }
  function seedPromotions(){
    var t=new Date(); t.setHours(0,0,0,0);
    return [
      {id:'p1',title:'Summer Escape',description:'Book your stay this month and enjoy an exclusive sunset escape with breakfast included on select suites and premium rooms.',percent:25,start:fmtDateISO(addDays(t,-10)),end:fmtDateISO(addDays(t,14)),code:'SUMMER25',rooms:['r2','r8','r9','r12'],minStay:2,status:'active',featured:true},
      {id:'p2',title:'Autumn Serenity',description:'Embrace quieter mornings with 15% off garden suites for the upcoming season.',percent:15,start:fmtDateISO(addDays(t,30)),end:fmtDateISO(addDays(t,75)),code:'AUTUMN15',rooms:['r3','r10'],minStay:2,status:'upcoming',featured:false},
      {id:'p3',title:'Spring Blossom',description:'A seasonal welcome for early-year travellers.',percent:10,start:fmtDateISO(addDays(t,-70)),end:fmtDateISO(addDays(t,-40)),code:'SPRING10',rooms:[],minStay:1,status:'expired',featured:false}
    ];
  }
  function seedPromoCodes(){
    var t=new Date(); t.setHours(0,0,0,0);
    return [
      {id:'pc1',code:'SUMMER25',type:'percent',value:25,minAmount:0,start:fmtDateISO(addDays(t,-10)),end:fmtDateISO(addDays(t,14)),roomId:null,usageLimit:200,used:41,status:'active',desc:'Summer Escape: 25% off'},
      {id:'pc2',code:'WELCOME10',type:'percent',value:10,minAmount:0,start:fmtDateISO(addDays(t,-90)),end:fmtDateISO(addDays(t,90)),roomId:null,usageLimit:300,used:118,status:'active',desc:'Welcome discount for new guests'},
      {id:'pc3',code:'STAY5',type:'percent',value:5,minAmount:300,start:fmtDateISO(addDays(t,-20)),end:fmtDateISO(addDays(t,30)),roomId:null,usageLimit:150,used:36,status:'active',desc:'Extra 5% on bookings over $300'},
      {id:'pc4',code:'ROOMSPECIAL20',type:'percent',value:20,minAmount:0,start:fmtDateISO(addDays(t,-15)),end:fmtDateISO(addDays(t,20)),roomId:'r1',usageLimit:80,used:22,status:'active',desc:'20% off Deluxe King Room only'},
      {id:'pc5',code:'EXPVR50',type:'percent',value:50,minAmount:0,start:fmtDateISO(addDays(t,-40)),end:fmtDateISO(addDays(t,-10)),roomId:null,usageLimit:20,used:20,status:'expired',desc:'Expired flash deal'},
      {id:'pc6',code:'LUXEFREE',type:'fixed',value:50,minAmount:400,start:fmtDateISO(addDays(t,-5)),end:fmtDateISO(addDays(t,40)),roomId:null,usageLimit:60,used:9,status:'active',desc:'$50 off stays over $400'}
    ];
  }
  function seedFood(){
    return [
      {id:'f1',cat:'Breakfast',name:'Continental Breakfast',desc:'Fresh pastries, granola, yogurt and seasonal fruit with coffee or tea.',price:18,img:ph('breakfast',500)},
      {id:'f2',cat:'Breakfast',name:'Full English Deluxe',desc:'Eggs, smoked bacon, sausage, grilled tomato, mushrooms and toast.',price:24,img:ph('av',500)},
      {id:'f3',cat:'Breakfast',name:'Pancake Stack',desc:'Fluffy buttermilk pancakes with maple syrup, banana and pecans.',price:12,img:ph('coffee',500)},
      {id:'f4',cat:'Lunch',name:'Grilled Salmon',desc:'Atlantic salmon, citrus beurre blanc, asparagus and crushed potatoes.',price:26,img:ph('food1',500)},
      {id:'f5',cat:'Lunch',name:'Steak & Crisp Fries',desc:'Prime sirloin char-grilled to order with herb butter and fries.',price:29,img:ph('food2',500)},
      {id:'f6',cat:'Dinner',name:'Margherita Pizza',desc:'San Marzano tomato, fresh mozzarella and basil on a wood-fired base.',price:16,img:ph('food3',500)},
      {id:'f7',cat:'Dinner',name:'Alfredo Primavera',desc:'Creamy fettuccine with asparagus, peas and parmesan crisp.',price:18,img:ph('food4',500)},
      {id:'f8',cat:'Snacks',name:'Cheese Nachos',desc:'Tortilla chips, melted cheddar, jalape\u00f1os, salsa and sour cream.',price:10,img:ph('food5',500)},
      {id:'f9',cat:'Drinks',name:'Fresh Mint Lemonade',desc:'Hand-pressed lemon, mint and cane sugar over ice.',price:5,img:ph('drink',500)},
      {id:'f10',cat:'Drinks',name:'Espresso Barrel',desc:'A double shot of our single-origin espresso.',price:4,img:ph('coffee',400)},
      {id:'f11',cat:'Services',name:'Airport Pickup',desc:'Private chauffeured transfer with welcome sign.',price:35,img:ph('car',500)},
      {id:'f12',cat:'Services',name:'Spa Session',desc:'60-minute aromatherapy massage in the rooftop spa.',price:45,img:ph('spa',500)},
      {id:'f13',cat:'Services',name:'Extra Bed',desc:'Premium rollaway bed with hotel-grade linens.',price:25,img:ph('bed4',500)},
      {id:'f14',cat:'Services',name:'Laundry Express',desc:'Same-day wash, press and fold for up to 5 items.',price:12,img:ph('towel',500)},
      {id:'f15',cat:'Services',name:'Late Checkout',desc:'Keep your room until 4 PM on departure day.',price:15,img:ph('clock2',500)},
      {id:'f16',cat:'Drinks',name:'Fresh Fruit Platter',desc:'A seasonal selection of tropical fruit for two.',price:9,img:ph('fruit',500)}
    ];
  }
  function seedGallery(){
    return [
      {id:'g1',title:'Hotel Exterior',cat:'hotel',img:ph('exteriorNight',900),featured:true},
      {id:'g2',title:'Grand Lobby',cat:'facility',img:ph('lobby',900),featured:false},
      {id:'g3',title:'Infinity Pool',cat:'facility',img:ph('pool',900),featured:false},
      {id:'g4',title:'Deluxe Suite',cat:'room',img:ph('suite',900),featured:false},
      {id:'g5',title:'Signature Restaurant',cat:'dining',img:ph('restaurant',900),featured:false},
      {id:'g6',title:'Rooftop Lounge',cat:'facility',img:ph('rooftop',900),featured:false},
      {id:'g7',title:'Spa & Wellness',cat:'facility',img:ph('spa',900),featured:false},
      {id:'g8',title:'Fitness Center',cat:'facility',img:ph('gym',900),featured:false},
      {id:'g9',title:'Premium Bedroom',cat:'room',img:ph('bed6',900),featured:false}
    ];
  }
  function seedNotifs(){
    var t=new Date(); t.setHours(0,0,0,0);
    var H=function(n){var d=new Date(t);d.setHours(d.getHours()-n);return d.getTime();};
    return [
      {id:'n1',type:'booking',title:'Booking confirmed',msg:'Your stay at the Deluxe King Room is confirmed.',time:H(2),read:false,link:'/user/booking-details?id=b1'},
      {id:'n2',type:'payment',title:'Payment due',msg:'Your payment of $672 for HB-2026-1001 is still unpaid.',time:H(9),read:false,link:'/user/payment-status'},
      {id:'n3',type:'checkin',title:'Check-in tomorrow',msg:'Your Grand Ocean Suite check-in is tomorrow at 3 PM.',time:H(24),read:false,link:'/user/booking-details?id=b2'},
      {id:'n4',type:'offer',title:'Summer Escape offer',msg:'25% off on premium suites with code SUMMER25.',time:H(50),read:true,link:'/offers'},
      {id:'n5',type:'payment',title:'Payment received',msg:'We received your payment for stay HB-2026-1004.',time:H(76),read:true,link:'/user/payment-status'},
      {id:'n6',type:'service',title:'Room service updated',msg:'Your spa appointment order was confirmed.',time:H(120),read:true,link:'/user/food-services'}
    ];
  }
  function seedContents(){
    return {
      hotelName:'',
      tagline:'Where the ocean meets effortless luxury',
      heroTitle:'Find Your Next Stay',
      heroSub:'Unwind in the heart of the coastline. Breathtaking sea views, five-star hospitality and moments that stay with you long after checkout.',
      heroBtn:'Explore Rooms',
      aboutTitle:'A destination in itself',
      aboutText:'Since 2016, Grand Meridian Resort has welcomed travellers from more than 60 countries to a quiet stretch of shoreline. Our philosophy is simple: anticipate, don\u2019t intrude. Every stay is shaped by calm, attentive service, considered design and the genuine warmth of our people. From the moment you arrive to the moment you leave, we\u2019re here to make the ordinary days feel like occasions.',
      aboutPoints:['Beachfront luxury resort','150+ rooms & suites','Fine dining & rooftop bar','Spa, pool & wellness'],
      phone:'+880 3421 555 100',
      email:'hello@grandmeridian.com',
      address:'51 Marine Drive, Cox\u2019s Bazar 4700, Bangladesh',
      supportHours:'Front desk open 24/7 \u2022 Reservations 8 AM \u2013 11 PM',
      latitude:'21.4272',
      longitude:'91.9773',
      socials:{facebook:'#',instagram:'#',twitter:'#',youtube:'#'},
      stats:{rooms:'150+',labelRooms:'Rooms & Suites',guests:'50K+',labelGuests:'Happy Guests',years:'10+',labelYears:'Years of Hospitality',rating:'4.8/5',labelRating:'Average Guest Rating'},
      footerAbout:'A serene five-star beachfront resort crafted for restorative escapes along the Bay of Bengal.'
    };
  }
  function seedWifi(){
    return {
      hotel:{ssid:'GrandMeridian_Guest',password:'GM#Sunset2026',note:'Connects across the lobby, pool deck and public wings',instructions:'Select the network, open any page to sign in, then use the password below.'},
      floors:{
        '1':{ssid:'GrandMeridian_Floor1',password:'GM#FloorOne'},
        '2':{ssid:'GrandMeridian_Floor2',password:'GM#FloorTwo'},
        '4':{ssid:'GrandMeridian_Floor4',password:'GM#FloorFour'},
        '5':{ssid:'GrandMeridian_Floor5',password:'GM#FloorFive'},
        '6':{ssid:'GrandMeridian_Floor6',password:'GM#FloorSix'}
      },
      rooms:{'r1':{ssid:'Hotel_Guest_104',password:'GM#104Guest'},'r2':{ssid:'Hotel_Guest_501',password:'GM#501Ocean'},'r9':{ssid:'Hotel_Guest_404',password:'GM#404Suite'}}
    };
  }
  function seedSettings(){ return {currency:'USD',taxRate:0.1,siteName:'',supportEmail:'support@grandmeridian.com',maintenanceMode:false,allowRegistration:true,gridColumns:3}; }
  function seedOrders(){
    function f(n){return {month:'short',day:'numeric',hour:'numeric',minute:'2-digit'};}
    var d=function(h){ var x=new Date(); x.setHours(x.getHours()-h); return x.toLocaleDateString('en-US',f()); };
    function oid(){ return 'ORD-'+String(Math.floor(Math.random()*90000)+10000); }
    return [
      {id:'o1',orderId:oid(),userId:'u1',label:'Margherita Pizza x1, Fresh Mint Lemonade x2',total:26,where:'Room 104',date:d(2),status:'DELIVERED'},
      {id:'o2',orderId:oid(),userId:'u1',label:'Continental Breakfast x1, Espresso Barrel x2',total:26,where:'Room 104',date:d(26),status:'DELIVERED'},
      {id:'o3',orderId:oid(),userId:'u4',label:'Steak &amp; Crisp Fries x1',total:29,where:'Room 404',date:d(30),status:'DELIVERED'},
      {id:'o4',orderId:oid(),userId:'u5',label:'Alfredo Primavera x2, Spa Session x1',total:81,where:'Room 601',date:d(6),status:'OUT_FOR_DELIVERY'},
      {id:'o5',orderId:oid(),userId:'u1',label:'Rose De Luxe x1',total:18,where:'Lobby pickup',date:d(70),status:'CANCELLED'}
    ];
  }

  function ensureSeed(){
    var base = LS.get('hv_seed', null);
    if(base && base.v===1) return;
    var rooms=seedRooms(), users=seedUsers();
    var seed={v:1,rooms:rooms,users:users,promotions:[],promoCodes:[],food:[],gallery:[],notifs:[]};
    LS.set('hv_seedbase',{rooms:rooms,users:users});
    seed.promotions=seedPromotions();
    seed.promoCodes=seedPromoCodes();
    seed.food=seedFood();
    seed.gallery=seedGallery();
    seed.notifs=seedNotifs();
    var bookings=seedBookings();
    LS.set('hv_seed',seed);
    if(LS.get('hv_bookings',null)===null){ LS.set('hv_bookings',bookings); if(LS.get('hv_init',null)===null) LS.set('hv_init',{books:bookings}); }
    if(LS.get('hv_favorites',null)===null) LS.set('hv_favorites',{u1:['r2','r12']});
    if(LS.get('hv_contents',null)===null) LS.set('hv_contents',seedContents());
    if(LS.get('hv_wifi',null)===null) LS.set('hv_wifi',seedWifi());
    if(LS.get('hv_settings',null)===null) LS.set('hv_settings',seedSettings());
    if(LS.get('hv_orders',null)===null) LS.set('hv_orders',seedOrders());
    if(LS.get('hv_search',null)===null) LS.set('hv_search',null);
  }
  ensureSeed();
  function syncDemoUsers(){
    var demos=getDemoUsers();
    var hasCustom=LS.get('hv_users',null)!==null;
    var users=LS.get('hv_users',null);
    var base=LS.get('hv_seedbase',{users:[]}).users||[];
    var next=(hasCustom&&users&&users.length)?users:base;
    var merged=next.slice();
    demos.forEach(function(d){
      var idx=-1;
      for(var i=0;i<merged.length;i++){ if(merged[i].id&&merged[i].id===d.id){ idx=i; break; } }
      if(idx===-1){
        for(var j=0;j<merged.length;j++){ if((merged[j].email||'').toLowerCase()===d.email.toLowerCase()){ idx=j; break; } }
      }
      if(idx===-1){ var copy={}; for(var k in d)copy[k]=d[k]; merged.push(copy); return; }
      var cur=merged[idx];
      merged[idx].id=d.id; merged[idx].email=d.email; merged[idx].password=d.password;
      merged[idx].role=d.role; merged[idx].name=d.name; merged[idx].phone=d.phone||cur.phone||'';
      merged[idx].status=cur.status||'active';
    });
    LS.set('hv_users',merged);
    LS.set('hv_seedbase',{rooms:LS.get('hv_seedbase',{rooms:seedRooms()}).rooms,users:merged});
  }
  syncDemoUsers();

  function getRooms(){ if(window.__SERVER_ROOMS__) return window.__SERVER_ROOMS__; return LS.get('hv_rooms', LS.get('hv_seedbase',{rooms:[]}).rooms); }
  function saveRooms(r){ LS.set('hv_rooms',r); }
  function getUsers(){ if(window.__SERVER_USERS__) return window.__SERVER_USERS__; return LS.get('hv_users', LS.get('hv_seedbase',{users:[]}).users); }
  function saveUsers(u){ LS.set('hv_users',u); }
  function getBookings(){ if(window.__SERVER_BOOKINGS__) return window.__SERVER_BOOKINGS__; return LS.get('hv_bookings',[]); }
  function saveBookings(b){ LS.set('hv_bookings',b); }
  function getPromotions(){ if(window.__SERVER_PROMOS__) return window.__SERVER_PROMOS__; return LS.get('hv_promos', LS.get('hv_seed',{promotions:[]}).promotions); }
  function savePromotions(p){ LS.set('hv_promos',p); }
  function getPromoCodes(){ if(window.__SERVER_PROMOCODES__) return window.__SERVER_PROMOCODES__; return LS.get('hv_promocodes', LS.get('hv_seed',{promoCodes:[]}).promoCodes); }  function savePromoCodes(p){ LS.set('hv_promocodes',p); }
  function getGallery(){ if(window.__SERVER_GALLERY__) return window.__SERVER_GALLERY__; return LS.get('hv_gallery', LS.get('hv_seed',{gallery:[]}).gallery); }
  function saveGallery(g){ LS.set('hv_gallery',g); }
  function getFood(){ if(window.__SERVER_FOOD__) return window.__SERVER_FOOD__; return LS.get('hv_food', LS.get('hv_seed',{food:[]}).food); }
  function saveFood(f){ LS.set('hv_food',f); }
  function getNotifs(u){ var all=LS.get('hv_notifs', LS.get('hv_seed',{notifs:[]}).notifs); if(u){ return all.map(function(n){ return /user/.test(n.type)?n:n; }); } return all; }
  function saveNotifs(n){ LS.set('hv_notifs',n); }
  function addNotif(type,title,msg,link){
    var n={id:'n'+Date.now()+Math.floor(Math.random()*1000),type:type,title:title,msg:msg,link:link||'',time:Date.now(),read:false};
    var all=getNotifs(); all.unshift(n); saveNotifs(all);
    return n;
  }
  /**
   * Booking status/payment changes made by staff (confirm, check-in, check-out,
   * admin cancel) happen in a different browser/session than the guest's - so
   * there's no way to push a notification to the guest the instant it happens.
   * Instead, each time the guest's own browser loads real booking data, this
   * compares it against what was last seen for that booking (stored locally)
   * and raises a notification for whatever changed since last time.
   */
  function syncBookingNotifs(bookings){
    (bookings||[]).forEach(function(b){
      var key='hv_seen_booking_'+b.id;
      var prev=LS.get(key,null);
      var snap={status:b.status,payment:b.payment};
      if(prev){
        if(prev.status!==b.status){
          var msgs={
            confirmed:['booking','Booking confirmed','Your booking '+b.bookingId+' for '+b.roomName+' has been confirmed.'],
            active:['checkin','Checked in','You are now checked in for '+b.roomName+'. Enjoy your stay!'],
            completed:['checkin','Checked out','Thanks for staying at '+b.roomName+' - hope to see you again soon!'],
            cancelled:['booking','Booking cancelled','Your booking '+b.bookingId+' for '+b.roomName+' was cancelled.']
          };
          var m=msgs[b.status];
          if(m) addNotif(m[0],m[1],m[2],'/user/booking-details?id='+b.id);
        }
        if(prev.payment!==b.payment && b.payment==='paid'){
          addNotif('payment','Payment received','Your payment for booking '+b.bookingId+' was received. Thank you!','/user/booking-details?id='+b.id);
        }
      }
      LS.set(key,snap);
    });
  }
  function getContents(){ if(window.__SERVER_CONTENT__) return Object.assign({}, seedContents(), window.__SERVER_CONTENT__); return LS.get('hv_contents', seedContents()); }  function saveContents(c){ LS.set('hv_contents',c); }
  function getWifi(){
    var w=LS.get('hv_wifi',seedWifi());
    if(window.__SERVER_WIFI__){ w=JSON.parse(JSON.stringify(w)); w.hotel=window.__SERVER_WIFI__; }
    return w;
  }
  function saveWifi(w){ LS.set('hv_wifi',w); }
  function getSettings(){ return LS.get('hv_settings',seedSettings()); }
  function saveSettings(s){ LS.set('hv_settings',s); }
  function getOrders(){ if(window.__SERVER_ORDERS__) return window.__SERVER_ORDERS__; return LS.get('hv_orders',[]); }
  function saveOrders(o){ LS.set('hv_orders',o); }
  function getSearch(){ return LS.get('hv_search',null); }
  function saveSearch(s){ LS.set('hv_search',s); }

  function roomById(id){ return getRooms().filter(function(r){return String(r.id)===String(id);})[0]; }
  function userById(id){ return getUsers().filter(function(u){return u.id===id;})[0]; }
  function bookingById(id){ return getBookings().filter(function(b){return b.id===id||b.bookingId===id;})[0]; }
  function activePromo(){ var all=getPromotions(); var t=today(); return all.filter(function(p){return p.status==='active'&&parseISO(p.start)<=t&&parseISO(p.end)>=t;})[0]; }
  function getActivePromos(){ var promos=getPromotions(); var t=today(); return promos.filter(function(p){return p.status==='active'&&parseISO(p.start)<=t&&parseISO(p.end)>=t;}); }

  /* current user */
  var CURRENT_USER_KEY='hv_cu';
  function cookieUser(){
    try{
      var m=document.cookie.split('; ');
      for(var i=0;i<m.length;i++){
        var p=m[i].indexOf('=');
        if(p===-1)continue;
        var k=m[i].substring(0,p);
        // The server encodes this cookie value the way
        // java.net.URLEncoder does (application/x-www-form-urlencoded),
        // which turns spaces into '+'. decodeURIComponent alone does NOT
        // convert '+' back to a space (it only understands %XX escapes),
        // so raw names/emails came through with literal '+' characters
        // instead of spaces (e.g. "Fazley+Rabbi"). Replacing '+' with a
        // space BEFORE decoding matches how the value was actually encoded.
        var raw=m[i].substring(p+1).replace(/\+/g,' ');
        var v=decodeURIComponent(raw);
        if(k==='hv_user'&&v){ return JSON.parse(v); }
      }
    }catch(e){}
    return null;
  }
  function getCurrentUser(){
    var cu=cookieUser();
    if(cu&&cu.email) return cu; // the cookie is set server-side (LoginSuccessHandler) with the real DB id - trust it as-is
    return LS.get(CURRENT_USER_KEY,null);
  }
  function setCurrentUser(u){ LS.set(CURRENT_USER_KEY,u); }
  function login(email,pass){
    var users=getUsers();
    var u=users.filter(function(x){return x.email.toLowerCase()===String(email).toLowerCase().trim();})[0];
    if(!u) return {ok:false,msg:'No account found with this email.'};
    if(u.password!==pass) return {ok:false,msg:'Incorrect password. Please try again.'};
    if(u.status==='blocked') return {ok:false,msg:'This account has been blocked by an administrator.'};
    if(u.status!=='active') return {ok:false,msg:'This account has been deactivated. Contact support.'};
    setCurrentUser(u);
    return {ok:true,user:u};
  }
  function register(data){
    var users=getUsers();
    if(users.filter(function(x){return x.email.toLowerCase()===data.email.toLowerCase();}).length) return {ok:false,msg:'An account with this email already exists.'};
    var u={id:'u'+Date.now(),name:data.name,email:data.email,password:data.password,phone:data.phone||'',role:'GUEST',status:'active',address:'',memberSince:new Date().toISOString(),image:'',bookings:0};
    users.push(u); saveUsers(users);
    var nl=LS.get('hv_notifs',LS.get('hv_seed',{notifs:[]}).notifs||[]);
    nl.unshift({id:'n'+Date.now(),type:'welcome',title:'Welcome aboard',msg:'Your account was created. Enjoy exploring Grand Meridian Resort.',time:Date.now(),read:false,user:'u'+Date.now(),link:'/user/profile'});
    saveNotifs(nl);
    setCurrentUser(u);
    return {ok:true,user:u};
  }
  function updateProfile(patch){
    var cu=getCurrentUser(); if(!cu)return null;
    var merge={}; var k; for(k in cu)merge[k]=cu[k]; for(k in patch)merge[k]=patch[k];
    setCurrentUser(merge);
    var users=getUsers().map(function(u){ if(u.id===merge.id) return merge; return u; });
    saveUsers(users);
    return merge;
  }
  // Resizes/compresses an uploaded photo client-side before it ever becomes a
  // base64 string. Without this, a normal phone photo (often 3-8MB) or an
  // AI-generated image easily exceeds the server's request-size limit once
  // base64-encoded (~33% larger again), which looks like the app "crashing"
  // on upload. Capping the longest side at 1600px and re-encoding as JPEG
  // keeps every upload small and fast regardless of the original file.
  function readImgFile(file,cb){
    var r=new FileReader();
    r.onload=function(){
      var original=String(r.result);
      var img=new Image();
      img.onload=function(){
        var maxSide=1600;
        var scale=Math.min(1,maxSide/Math.max(img.width,img.height));
        var w=Math.round(img.width*scale), h=Math.round(img.height*scale);
        var canvas=document.createElement('canvas');
        canvas.width=w; canvas.height=h;
        var ctx=canvas.getContext('2d');
        ctx.drawImage(img,0,0,w,h);
        try{
          cb(canvas.toDataURL('image/jpeg',0.82));
        }catch(e){
          // Canvas export can fail for some exotic formats/tainted sources -
          // fall back to the original rather than losing the upload entirely.
          cb(original);
        }
      };
      img.onerror=function(){ cb(original); };
      img.src=original;
    };
    r.readAsDataURL(file);
  }
  function clearAuthCookie(){
    document.cookie='hv_user=; Max-Age=0; path=/; SameSite=Lax';
  }
  function logout(){
    LS.remove(CURRENT_USER_KEY);
    clearAuthCookie();
  }

  function favs(){ var cu=getCurrentUser(); var id=cu?cu.id:'anon'; var all=LS.get('hv_favorites',{}); return all[id]||[]; }
  function saveFavs(list){ var cu=getCurrentUser(); var id=cu?cu.id:'anon'; var all=LS.get('hv_favorites',{}); all[id]=list; LS.set('hv_favorites',all); }
  function isFav(roomId){ return favs().indexOf(roomId)>-1; }
  function toggleFav(roomId){
    if(!getCurrentUser()){
      window.location.href='/login?next='+encodeURIComponent(window.location.pathname+window.location.search);
      return null; // caller should treat null as "not handled - redirecting"
    }
    var l=favs();
    if(l.indexOf(roomId)>-1){ saveFavs(l.filter(function(x){return x!==roomId;})); return false; }
    l.push(roomId); saveFavs(l); return true;
  }

  function bookNo(){
    var y=new Date().getFullYear();
    var c=LS.get('hv_lastseq',0)+1; LS.set('hv_lastseq',c);
    return 'HB-'+y+'-'+String(c).padStart(6,'0');
  }

  function promoResult(chips,roomId,subtotal){
    var mc=chips||null;
    if(!mc) return {ok:true,code:null,discount:0,msg:''};
    var all=getPromoCodes(); var pc=all.filter(function(x){return x.code.toUpperCase()===String(mc).toUpperCase().trim();})[0];
    if(!pc) return {ok:false,code:mc,discount:0,msg:'Promo code not found. Check the code and try again.'};
    if(pc.status!=='active'||parseISO(pc.end)<today()) return {ok:false,code:mc,discount:0,msg:'This promo code has expired.'};
    if(pc.minAmount&&subtotal<pc.minAmount) return {ok:false,code:mc,discount:0,msg:'This code requires a minimum booking of '+money(pc.minAmount)+'.'};
    if(pc.roomId&&pc.roomId!==roomId) { var rr=roomById(pc.roomId); return {ok:false,code:mc,discount:0,msg:'This code applies only to '+(rr?rr.name:'a specific room')+'.'}; }
    var disc = pc.type==='fixed' ? Math.min(pc.value,subtotal) : Math.round(subtotal*(pc.value/100));
    return {ok:true,code:pc.code,discount:disc,msg:'You saved '+money(disc)+'.'};
  }
  function priceBreakdown(room,checkIn,checkOut,roomsQty,services,promoCode){
    var r=room||{price:0,discount:0}; var n=nights(checkIn,checkOut); var qty=roomsQty||1;
    var basePerNight=r.price-r.price*(r.discount||0)/100;
    var roomTotal=Math.round(basePerNight*n*qty);
    var svcTotal=services||0;
    var pr=promoResult(promoCode,r.id,roomTotal);
    var disc=pr.discount;
    var tax=Math.round((roomTotal+svcTotal-disc)*TAX_RATE);
    var total=roomTotal+svcTotal-disc+tax;
    return {nights:n,base:r.price,discountPct:r.discount||0,nightly:Math.round(basePerNight),roomTotal:roomTotal,servicesTotal:svcTotal,promo:pr,tax:tax,total:total};
  }
  function createBooking(data){
    var list=getBookings();
    var r=roomById(data.roomId); var u=getCurrentUser()||userById(data.userId);
    var calc=priceBreakdown(r,data.checkIn,data.checkOut,data.rooms,data.servicesTotal,data.promoCode);
    var b={id:'b'+Date.now(),bookingId:data.bookingId||bookNo(),guest:data.guestName||(u?u.name:'Guest'),userId:u?u.id:'',roomId:r.id,roomName:r.name,roomType:r.type,roomImage:r.images[0],
      checkIn:data.checkIn,checkOut:data.checkOut,adults:data.adults||2,children:data.children||0,pets:!!data.pets,
      roomPrice:r.price,nightlyDisc:r.discount||0,nights:calc.nights,totalAmount:calc.total,discountAmount:Math.round(calc.roomTotal-calc.roomTotal+calc.discountPct?data.actualDisc||calc.promo.discount:0),servicesTotal:calc.servicesTotal,taxAmount:calc.tax,
      payment:data.payment||'unpaid',status:data.status||'confirmed',services:data.serviceIds||[],promoCode:calc.promo.code||null,
      contact:{name:data.guestName,email:data.email,phone:data.phone,address:data.address,request:data.request||''},
      createdAt:todayISO()};
    list.unshift(b); saveBookings(list);
    var nl=LS.get('hv_notifs',[]); nl.unshift({id:'n'+Date.now(),title:'Booking confirmed',msg:'Your booking '+b.bookingId+' is confirmed. See you soon!',time:Date.now(),read:false,link:'/user/booking-details?id='+b.id});
    saveNotifs(nl);
    return b;
  }

  /* ---------- live stay engine ---------- */
  var CHECK_IN_TIME='15:00', CHECK_OUT_TIME='12:00';
  function dtAt(date,hhmm){
    var d=new Date(date); d.setHours(0,0,0,0);
    var parts=String(hhmm||'15:00').split(':');
    d.setHours(+parts[0]||15,(+parts[1])||0,0,0);
    return d;
  }
  function bookingCheckInDT(b){ return dtAt(parseISO(b.checkIn), b.checkInTime||CHECK_IN_TIME); }
  function bookingCheckOutDT(b){ return dtAt(parseISO(b.checkOut), b.checkOutTime||CHECK_OUT_TIME); }
  function countdown(targetMs,nowMs){
    var diff=Math.max(0,(targetMs||0)-(nowMs==null?Date.now():nowMs));
    return {d:Math.floor(diff/86400000),h:Math.floor(diff%86400000/3600000),m:Math.floor(diff%3600000/60000),s:Math.floor(diff%60000/1000),days:Math.floor(diff/86400000),hours:Math.floor(diff%86400000/3600000),minutes:Math.floor(diff%3600000/60000),seconds:Math.floor(diff%60000/1000),total:diff};
  }
  function fmtCountdown(c){
    var s='';
    if(c.d)s+=c.d+'d ';
    s+=String(c.h).padStart(2,'0')+':'+String(c.m).padStart(2,'0')+':'+String(c.s).padStart(2,'0');
    return s.trim();
  }
  /* Returns one of: UPCOMING / CHECK_IN_TODAY / AWAITING_CHECK_IN / CHECKED_IN / CURRENT_STAY / CHECK_OUT_TODAY / CHECK_OUT_DUE / COMPLETED / CANCELLED */
  var STAY_STATES={
    UPCOMING:{label:'Upcoming stay',tone:'primary'},
    CHECK_IN_TODAY:{label:'Check-in today',tone:'accent'},
    AWAITING_CHECK_IN:{label:'Awaiting check-in',tone:'warning'},
    CHECKED_IN:{label:'Currently staying',tone:'success'},
    CURRENT_STAY:{label:'Currently staying',tone:'success'},
    CHECK_OUT_TODAY:{label:'Check-out today',tone:'warning'},
    CHECK_OUT_DUE:{label:'Check-out due',tone:'danger'},
    COMPLETED:{label:'Stay completed',tone:'info'},
    CANCELLED:{label:'Cancelled',tone:'danger'}
  };
  function liveStay(b){
    if(!b)return {state:'NONE',remaining:0,progress:0,target:0,confirmed:STAY_STATES.UPCOMING};
    if(b.status==='cancelled')return {state:'CANCELLED',remaining:0,progress:0,target:0,meta:STAY_STATES.CANCELLED};
    var now=Date.now();
    var cinDT=bookingCheckInDT(b).getTime(), coutDT=bookingCheckOutDT(b).getTime();
    var hasIn=!!b.actualCheckInTime||b.status==='active';
    var hasOut=!!b.actualCheckOutTime||b.status==='completed';
    var state, target=0;
    if(hasOut){
      state='COMPLETED';
    } else if(hasIn){
      if(now>=coutDT){ state='CHECK_OUT_DUE'; }
      else if(todayISO()===b.checkOut){ state='CHECK_OUT_TODAY'; target=coutDT; }
      else { state='CURRENT_STAY'; target=coutDT; }
    } else {
      if(now<cinDT){
        if(todayISO()===b.checkIn){ state='CHECK_IN_TODAY'; target=cinDT; }
        else { state='UPCOMING'; target=cinDT; }
      } else {
        state='AWAITING_CHECK_IN'; target=0;
      }
    }
    var total=coutDT-cinDT, prog=total>0?Math.max(0,Math.min(100,Math.round((now-cinDT)/total*100))):(hasOut?100:0);
    if(state==='COMPLETED')prog=100;
    if(state==='UPCOMING'||state==='CHECK_IN_TODAY'||state==='AWAITING_CHECK_IN')prog=0;
    // countdown() expects an ABSOLUTE target timestamp (it subtracts "now"
    // internally) - target above is already absolute (cinDT/coutDT), not a
    // pre-computed duration, so the countdown correctly ticks down on every
    // render instead of freezing at 0.
    return {state:state,remaining:countdown(target,now),progress:prog,target:target,meta:STAY_STATES[state]||STAY_STATES.UPCOMING,checkIn:cinDT,checkOut:coutDT};
  }
  function stayProgress(b){ return liveStay(b).progress; }
  function doCheckIn(id){
    var b=bookingById(id); if(!b)return {ok:false,msg:'Booking not found.'};
    var books=getBookings().map(function(x){ if(x.id===id){ x.status='active'; x.actualCheckInTime=new Date().toISOString(); } return x; });
    saveBookings(books);
    var room=roomById(b.roomId);
    if(room){ saveRooms(getRooms().map(function(r){ if(r.id===b.roomId){ r.status='occupied'; } return r; })); }
    var nl=LS.get('hv_notifs',[]); nl.unshift({id:'n'+Date.now(),type:'checkin',title:'Guest checked in',msg:b.guest+' checked into '+b.roomName+' ('+b.bookingId+').',time:Date.now(),read:false,link:'/admin/check-ins'});
    saveNotifs(nl);
    return {ok:true,booking:bookingById(id)};
  }
  function doCheckOut(id){
    var b=bookingById(id); if(!b)return {ok:false,msg:'Booking not found.'};
    var books=getBookings().map(function(x){ if(x.id===id){ x.status='completed'; x.actualCheckOutTime=new Date().toISOString(); x.payment='paid'; } return x; });
    saveBookings(books);
    var room=roomById(b.roomId);
    if(room){ saveRooms(getRooms().map(function(r){ if(r.id===b.roomId){ r.status='cleaning'; } return r; })); }
    var nl=LS.get('hv_notifs',[]); nl.unshift({id:'n'+Date.now(),type:'checkin',title:'Guest checked out',msg:b.guest+' checked out of '+b.roomName+' ('+b.bookingId+'). Room sent to housekeeping.',time:Date.now(),read:false,link:'/admin/check-outs'});
    saveNotifs(nl);
    return {ok:true,booking:bookingById(id)};
  }

  /* admin utilities: date + form helpers */
  function toDate(v){
    if(v==null||v==='')return null;
    if(v instanceof Date)return v;
    if(typeof v==='number')return new Date(v);
    if(typeof v==='string'){
      var m=/^(\d{4})-(\d{1,2})-(\d{1,2})/.exec(v);
      if(m)return new Date(+m[1],+m[2]-1,+m[3]);
      return new Date(v);
    }
    return null;
  }
  function month(v){ var d=toDate(v); return d&&d.getTime()?d.toLocaleDateString('en-US',{month:'short',year:'numeric'}):''; }
  function dateStr(v){ var d=toDate(v); return d&&d.getTime()?fmtDate(d):''; }
  function shortDate(v){ var d=toDate(v); return d&&d.getTime()?d.toLocaleDateString('en-US',{month:'short',day:'numeric'}):''; }
  function toInput(v){ var d=toDate(v); return d&&d.getTime()?fmtDateISO(d):''; }
  function toTs(v){ var d=toDate(v); return d&&d.getTime()?d:null; }
  function openForm(title,fields,submitLabel){
    submitLabel=submitLabel||'Save';
    var body=fields.map(function(f){
      var val=f.value==null?'':f.value;
      var req=f.req?'<span class="req">*</span>':'';
      var inner='';
      if(f.type==='label'){ inner='<div class="text-sm" style="padding:.5rem 0;color:var(--text);font-weight:600">'+esc(val)+'</div>'; }
      else if(f.type==='select'){
        var opts=(f.options||[]).map(function(o){ var ov=o,lbl=o; if(String(o).indexOf(':')>-1){ var sp=o.split(':'); ov=sp[0]; lbl=sp[1]; } return '<option value="'+esc(ov)+'"'+(String(ov)===String(val)?' selected':'')+'>'+esc(lbl)+'</option>'; }).join('');
        inner='<select class="input" id="'+f.id+'"'+(f.disabled?' disabled':'')+'>'+opts+'</select>';
      }
      else if(f.type==='textarea'){ inner='<textarea class="input" id="'+f.id+'" rows="3" placeholder="'+esc(f.placeholder||'')+'">'+esc(val)+'</textarea>'; }
      else { var t=f.type||'text'; if(t==='date')val=toInput(val); inner='<input class="input" id="'+f.id+'" type="'+t+'" value="'+esc(val)+'" placeholder="'+esc(f.placeholder||'')+'"'+(f.disabled?' disabled':'')+'>'; }
      return '<div class="field" data-fid="'+f.id+'"><label>'+esc(f.label)+' '+req+'</label>'+inner+'<div class="err">This field is required.</div></div>';
    }).join('');
    var m=openModal(
        '<div class="modal"><div class="modal-head"><h3>'+esc(title)+'</h3><button class="modal-x" aria-label="Close">&times;</button></div>'+
        '<div class="modal-body">'+body+'</div>'+
        '<div class="modal-foot"><button class="btn btn-ghost" data-c="1">Cancel</button><button class="btn btn-primary" data-ok="1">'+esc(submitLabel)+'</button></div></div>'
    );
    return new Promise(function(res){
      function build(){
        var out={},ok=true;
        fields.forEach(function(f){
          if(f.type==='label'){ out[f.id]=f.value; return; }
          var el=m.q('#'+f.id), v=el?String(el.value).trim():'', wrap=m.q('[data-fid="'+f.id+'"]');
          if(f.req&&!v){ ok=false; if(wrap)wrap.classList.add('show-err'); }
          else if(wrap)wrap.classList.remove('show-err');
          out[f.id]=v;
        });
        return ok?out:null;
      }
      m.q('[data-c]').addEventListener('click',function(){ m.close(); res(null); });
      m.q('[data-ok]').addEventListener('click',function(){ var out=build(); if(out){ m.close(); res(out); } });
      m.q('.modal-body').addEventListener('keydown',function(e){ if(e.key==='Enter'&&e.target.tagName!=='TEXTAREA'){ e.preventDefault(); m.q('[data-ok]').click(); } });
    });
  }

  /* ui: toast, modal */
  function toast(msg,type){
    type=type||'success';
    var root=document.querySelector('.toasts');
    if(!root){ root=document.createElement('div'); root.className='toasts'; document.body.appendChild(root); }
    var t=document.createElement('div'); t.className='toast '+type;
    var ic=type==='success'?icon('checkCircle'):(type==='error'?icon('alertCircle'):(type==='warning'?icon('alert'):icon('info')));
    t.className='toast '+type;
    t.innerHTML='<span class="tico">'+ic+'</span><div><div class="ttitle">'+esc(msg)+'</div></div>';
    root.appendChild(t);
    setTimeout(function(){ t.classList.add('out'); setTimeout(function(){ if(t.parentNode)t.parentNode.removeChild(t); },260); },3400);
  }
  var MODAL_COUNT=0;
  function openModal(html,cls){
    var m=document.createElement('div'); m.className='modal-back'; m.dataset.n=MODAL_COUNT++;
    var modal=document.createElement('div'); modal.className='modal '+(cls||''); modal.setAttribute('role','dialog'); modal.setAttribute('aria-modal','true');
    modal.innerHTML=html;
    m.appendChild(modal);
    document.body.appendChild(m);
    requestAnimationFrame(function(){ m.classList.add('open'); });
    function close(){ m.classList.remove('open'); document.body.removeChild(m); }
    m.addEventListener('click',function(e){ if(e.target===m) close(); });
    var x=modal.querySelector('.modal-x'); if(x) x.addEventListener('click',close);
    var esc2=function(e){ if(e.key==='Escape'){ close(); document.removeEventListener('keydown',esc2);} };
    document.addEventListener('keydown',esc2);
    return {el:m,close:close,q:function(sel){return modal.querySelector(sel);},all:function(sel){return modal.querySelectorAll(sel);}};
  }
  function openConfirm(title,msg,options){
    options=options||{};
    var danger=options.variant||'danger';
    var btnTxt=options.confirmText||'Confirm';
    var okBtn = danger==='success' ? 'btn-success':'btn-danger';
    var m=openModal(
        '<div class="modal-head"><h3>'+esc(title)+'</h3><button class="modal-x" aria-label="Close">&times;</button></div>'+
        '<div class="modal-body"><p style="font-size:.92rem;color:var(--muted)">'+msg+'</p></div>'+
        '<div class="modal-foot"><button class="btn btn-ghost" data-c="1">Cancel</button><button class="btn '+okBtn+'" data-ok="1">'+esc(btnTxt)+'</button></div>',
        'sm'
    );
    return new Promise(function(res){
      m.q('[data-c]').addEventListener('click',function(){ m.close(); res(false); });
      m.q('[data-ok]').addEventListener('click',function(){ m.close(); res(true); });
    });
  }
  function fieldErrors(fields){
    var clean=true;
    fields.forEach(function(f){
      var el=document.getElementById(f.id); var fw=el.closest('.field');
      var val=f.trim?String(el.value).trim():el.value;
      if(f.req&&!val){ if(fw)fw.classList.add('show-err'); if(!fw&&el)el.classList.add('invalid'); clean=false; return; }
      if(f.req&&val&&f.pattern&&!f.pattern.test(val)){ if(fw)fw.classList.add('show-err'); clean=false; return; }
      if(f.type==='confirm'){
        var a=val,b=(f.match?document.getElementById(f.match).value:null);
        if(!f.eq(a,b)){ if(fw)fw.classList.add('show-err'); clean=false; }
        return;
      }
      if(fw)fw.classList.remove('show-err');
      if(el)el.classList.remove('invalid');
    });
    return clean;
  }
  function clearErrors(rootEl){
    (rootEl||document).querySelectorAll('.field.show-err').forEach(function(el){ el.classList.remove('show-err'); });
    (rootEl||document).querySelectorAll('.input.invalid').forEach(function(el){ el.classList.remove('invalid'); });
  }

  /* charts (lightweight, dependency-free) */
  var Charts={};
  Charts.bar=function(el,labels,series,opts){
    opts=opts||{};
    var colors=opts.colors||['var(--primary)','var(--info)','var(--accent)','var(--danger)','var(--success)'];
    var max=0; series.forEach(function(s){ s.data.forEach(function(v){ if(v>max)max=v; }); });
    var seriesN=series.length;
    var boxH=el.clientHeight||240, axisH=28, legendH=(opts.legend===false?0:34), padT=24;
    var availH=Math.max(30,boxH-padT-axisH-legendH);
    var barW=Math.min(opts.barW||(seriesN>1?24:40),46);
    var colSel=opts.colSel||function(si){return colors[si%colors.length];};
    el.innerHTML='<div class="hb" style="display:flex;flex-direction:column;height:100%;min-height:'+boxH+'px">'+
        '<div class="hb-body" style="flex:1;display:flex;align-items:flex-end;gap:'+(opts.gap||12)+'px;padding:0 4px">'+
        labels.map(function(l,i){
          return '<div style="flex:1;display:flex;flex-direction:row;align-items:flex-end;justify-content:center;gap:'+(opts.groupGap||5)+'px;height:100%">'+
              series.map(function(s,si){
                var v=s.data[i], h=max?Math.round((v/max)*availH):0;
                return '<div style="width:'+barW+'px;height:'+h+'px;background:'+colSel(si)+';border-radius:6px 6px 2px 2px;position:relative;transition:height .5s"'+
                    ' title="'+esc(s.name+' - '+l+': '+v)+'">'+
                    (v>0?'<span style="position:absolute;bottom:calc(100% + 4px);left:0;right:0;text-align:center;font-size:11px;color:var(--muted);font-weight:600" class="hb-v">'+v+'</span>':'')+
                    '</div>';
              }).join('')+'</div>';
        }).join('')+'</div>'+
        '<div class="hb-x" style="display:flex;gap:'+(opts.gap||12)+'px;margin-top:10px;padding-top:6px;border-top:1px dashed var(--border)">'+
        labels.map(function(l){ return '<div style="flex:1;text-align:center;font-size:11px;color:var(--muted);white-space:nowrap;text-overflow:ellipsis;overflow:hidden">'+esc(l)+'</div>'; }).join('')+'</div>'+
        (opts.legend!==false?'<div class="hb-legend" style="display:flex;gap:14px;flex-wrap:wrap;margin-top:12px;justify-content:center">'+
            series.map(function(s,si){ return '<span style="display:inline-flex;align-items:center;gap:6px;font-size:12px;color:var(--muted)"><i style="width:10px;height:10px;border-radius:3px;background:'+colSel(si)+'"></i>'+esc(s.name)+'</span>'; }).join('')+'</div>':'')+
        '</div>';
  };
  Charts.line=function(el,labels,datasets,opts){
    opts=opts||{};
    var colors=opts.colors||['var(--primary)','var(--accent)','var(--danger)'];
    var n=labels.length;
    var max=0; datasets.forEach(function(d){ d.data.forEach(function(v){ if(v>max)max=Math.max(max,v); }); });
    var W=opts.w||el.clientWidth||560, H=opts.h||200, padL=34, padB=22, padT=14, padR=8;
    var iw=W-padL-padR, ih=H-padT-padB;
    var px=function(i){ return padL+(n>1?i*(iw/(n-1)):iw/2); };
    var py=function(v){ return padT+ih-(max? (v/max)*ih :0); };
    var g=[]; g.push('<svg viewBox="0 0 '+W+' '+H+'" style="width:100%;height:auto" role="img">');
    g.push('<line x1="'+padL+'" y1="'+py(0)+'" x2="'+(W-padR)+'" y2="'+py(0)+'" stroke="var(--border)" stroke-width="1"/>');
    for(var gi=0;gi<=4;gi++){ var vv=max*gi/5; var y=py(vv); g.push('<line x1="'+padL+'" y1="'+y+'" x2="'+(W-padR)+'" y2="'+y+'" stroke="var(--border)" stroke-width="1" stroke-dasharray="3 4"/>');
      g.push('<text x="'+(padL-8)+'" y="'+(y+4)+'" text-anchor="end" font-size="10" fill="var(--muted)">'+vv+'</text>'); }
    labels.forEach(function(l,i){ g.push('<text x="'+px(i)+'" y="'+(H-6)+'" text-anchor="middle" font-size="10" fill="var(--muted)">'+esc(l)+'</text>'); });
    datasets.forEach(function(d,di){
      var pts=d.data.map(function(v,i){ return px(i)+','+py(v); });
      g.push('<polyline points="'+pts.join(' ')+'" fill="none" stroke="'+colors[di%colors.length]+'" stroke-width="2.4" stroke-linecap="round" stroke-linejoin="round"/>');
      d.data.forEach(function(v,i){
        g.push('<circle cx="'+px(i)+'" cy="'+py(v)+'" r="3.4" fill="'+colors[di%colors.length]+'"/><title>'+esc(labels[i]+': '+v)+'</title>');
      });
    });
    g.push('</svg>');
    el.innerHTML=g.join('');
  };
  Charts.donut=function(el,items,opts){
    opts=opts||{};
    var colors=opts.colors||['var(--primary)','var(--accent)','var(--info)','var(--warning)','var(--danger)','var(--success)'];
    var total=items.reduce(function(a,b){return a+b.value;},0)||1;
    var R=opts.r||44, C=2*Math.PI*R;
    var offs=0, out=[];
    out.push('<svg viewBox="0 0 120 120" style="width:100%;max-width:180px;display:block;margin:0 auto;transform:rotate(-90deg)" role="img">');
    items.forEach(function(it,i){
      var frac=it.value/total, len=frac*C;
      var dash=offs+' '+(C-offs);
      out.push('<circle cx="60" cy="60" r="'+R+'" fill="none" stroke="'+colors[i%colors.length]+'" stroke-width="'+(opts.sw||15)+'" stroke-dasharray="'+len+' '+(C-len)+'" stroke-dashoffset="'+-offs+'" stroke-linecap="butt"><title>'+esc(it.label+': '+it.value)+'</title></circle>');
      offs+=len;
    });
    out.push('<circle cx="60" cy="60" r="'+R+'" fill="none" stroke="var(--surface)" stroke-width="'+(opts.sw||15)+'" stroke-dasharray="0.1 999" opacity="0"/>');
    out.push('</svg>');
    out.push('<div style="display:flex;flex-wrap:wrap;gap:8px 16px;justify-content:center;margin-top:12px">');
    items.forEach(function(it,i){
      out.push('<span style="display:inline-flex;align-items:center;gap:6px;font-size:12px;color:var(--muted)"><i style="width:10px;height:10px;border-radius:3px;background:'+colors[i%colors.length]+'"></i>'+esc(it.label)+' <b style="color:var(--text)">'+it.value+'</b></span>');
    });
    out.push('</div>');
    el.innerHTML=out.join('');
  };

  /* theme */
  var THEME_KEY='hv_theme';
  function getTheme(){ return LS.get(THEME_KEY,(window.matchMedia&&matchMedia('(prefers-color-scheme: dark)').matches)?'dark':'light'); }
  function applyTheme(t){
    document.documentElement.setAttribute('data-theme',t);
    document.querySelectorAll('[data-theme-icon]').forEach(function(b){ b.innerHTML=icon(t==='dark'?'sun':'moon','icon'); });
  }
  function initTheme(){ applyTheme(getTheme()); }
  function toggleTheme(){ var t=getTheme()==='dark'?'light':'dark'; LS.set(THEME_KEY,t); applyTheme(t); return t; }
  function wireTheme(){
    document.addEventListener('click',function(e){
      var b=e.target.closest('[data-theme-toggle]');
      if(b){ toggleTheme(); }
    });
  }

  /* routing guards */
  var LOGIN='/login';
  var P403='/403';
  function suRole(){ var u=getCurrentUser(); return u?u.role:null; }
  function redirectFor(role){
    if(role==='ADMIN'||role==='SUPER_ADMIN'||role==='STAFF') return '/admin/dashboard';
    return '/user/dashboard';
  }
  function staffRoles(){ return ['ADMIN','SUPER_ADMIN','STAFF']; }
  function boot(cfg){
    cfg=cfg||{};
    var u=getCurrentUser(); var role=suRole();
    if(cfg.guard){
      if(cfg.guard==='auth'){ if(!u){ location.replace(LOGIN+'?next='+encodeURIComponent(location.pathname.split('/').pop()+location.search)); return; } }
      if(cfg.guard==='admin'){ if(!u||(role!=='ADMIN'&&role!=='SUPER_ADMIN')){ location.replace(role?(P403):(LOGIN+'?next='+encodeURIComponent(location.pathname.split('/').pop()))); return; } }
      if(cfg.guard==='staff'){ if(!u||staffRoles().indexOf(role)===-1){ location.replace(role?(P403):(LOGIN+'?next='+encodeURIComponent(location.pathname.split('/').pop()))); return; } }
      if(cfg.guard==='user'){ if(!u){ location.replace(LOGIN+'?next='+encodeURIComponent(location.pathname.split('/').pop())); return; } if(role!=='GUEST'){ location.replace(redirectFor(role)); return; } }
      if(cfg.guard==='super'){ if(!u||role!=='SUPER_ADMIN'){ location.replace(P403); return; } }
      if(cfg.guard==='dash'){ if(!u){ location.replace(LOGIN+'?next='+encodeURIComponent(location.pathname.split('/').pop())); return; } }
      if(cfg.guard==='guest'){ if(u){ location.replace(redirectFor(role)); return; } }
      if(cfg.guard==='public'){ if(u&&role==='GUEST'){ location.replace(redirectFor(role)); return; } }
      if(cfg.guard==='adminpublic'){ if(u&&(role==='ADMIN'||role==='SUPER_ADMIN')){} else if(u&&role==='GUEST'){ location.replace(redirectFor(role)); return; } }
    }
    initTheme(); wireTheme(); wireDD(); hydrateAll();
    if(document.querySelector('nav[data-site-nav]')){
      renderPublicChrome(getActiveKey());
    }
    return {user:u,role:role};
  }

  function getActiveKey(){
    var a=document.body?document.body.getAttribute('data-active'):'';
    return a||'home';
  }

  /* global DOM helpers */
  function $(sel,root){ return (root||document).querySelector(sel); }
  function $$(sel,root){ return Array.prototype.slice.call((root||document).querySelectorAll(sel)); }
  function debounce(fn,ms){ var t; return function(){ var a=arguments,ctx=this; clearTimeout(t); t=setTimeout(function(){ fn.apply(ctx,a); },ms||250); }; }
  function emptyState(ico,title,msg,btn){
    return '<div class="empty"><div class="eico">'+icon(ico,'icon')+'</div><h3>'+esc(title)+'</h3><p>'+esc(msg)+'</p>'+ (btn||'') +'</div>';
  }
  function skeletonCard(){ return '<div class="room-card"><div class="sk" style="height:210px;border-radius:0"></div><div style="padding:1.1rem 1.2rem;display:flex;flex-direction:column;gap:.5rem"><div class="sk" style="height:16px;width:45%"></div><div class="sk" style="height:12px;width:80%"></div><div class="sk" style="height:12px;width:60%"></div><div class="sk" style="height:30px;width:100%;margin-top:.3rem"></div></div></div>'; }
  function skeletonRows(n){ var s=''; for(var i=0;i<n;i++){ s+='<div style="padding:.9rem 1rem;display:flex;gap:1rem;align-items:center"><div class="sk" style="height:40px;width:40px;border-radius:50%"></div><div style="flex:1;display:flex;flex-direction:column;gap:.4rem"><div class="sk" style="height:12px;width:45%"></div><div class="sk" style="height:10px;width:70%"></div></div></div>'; } return s; }
  function imgTag(src,alt,fallbackKey){
    return '<img src="'+src+'" alt="'+esc(alt||'')+'" loading="lazy" onerror="this.style.background=\'linear-gradient(135deg,#123f3b,#0a4a44)\';this.style.objectFit=\'cover\';this.removeAttribute(\'src\');this.style.display=\'none\';">';
  }  function imgsrc(src){
    return src;
  }
  function pagination(cur,total,fn){
    if(total<=1)return '';
    var out='<div class="pager">';
    var pages=[],i;
    for(i=1;i<=total;i++){
      if(i===1||i===total||Math.abs(i-cur)<=1){ pages.push(i); }
      else if(pages[pages.length-1]!=='…'){ pages.push('…'); }
    }
    pages.forEach(function(p){
      if(p==='…'){ out+='<button disabled style="border:0;cursor:default;background:none">…</button>'; }
      else{ out+='<button class="'+(p===cur?'active':'')+'" data-p="'+p+'">'+p+'</button>'; }
    });
    out+='</div>';
    return out;
  }
  function initPager(container,fn){
    $$('.pager button[data-p]',container).forEach(function(b){
      b.addEventListener('click',function(){ fn(parseInt(b.dataset.p,10)); });
    });
  }

  /* dynamic chrome: public site nav + footer, dashboard sidebar + topbar */
  var CONTENT=null;
  function getFilesContents(){ if(CONTENT)return CONTENT; CONTENT=getContents(); return CONTENT; }

  var PUBLIC_NAV=[{href:'/',label:'Home',key:'home'},{href:'/rooms',label:'Rooms',key:'rooms'},{href:'/about',label:'About',key:'about'},{href:'/contact',label:'Contact',key:'contact'},{href:'/offers',label:'Offers',key:'offers'}];
  var USER_NAV=[
    {label:'DASHBOARD'},
    {href:'/user/dashboard',icon:'home',label:'Dashboard',key:'ud'},
    {label:'BOOKING'},
    {href:'/user/search-rooms',icon:'bed',label:'Rooms',key:'ur'},
    {href:'/user/my-bookings',icon:'bookmark',label:'My Bookings',key:'ub'},
    {href:'/user/favorites',icon:'heart',label:'Favorites',key:'uf',count:'favs'},
    {label:'SERVICES'},
    {href:'/user/food-services',icon:'utensils',label:'Food & In-Room Services',key:'ufs'},
    {href:'/user/food-cart',icon:'inbox',label:'Your Orders',key:'uord'},
    {href:'/user/wifi',icon:'wifi',label:'WiFi',key:'uw'},
    {label:'PAYMENTS'},
    {href:'/user/payment-status',icon:'card',label:'Payments',key:'up'},
    {href:'/user/money-receipts',icon:'printer',label:'Money Receipts',key:'umr'},
    {label:'ACCOUNT'},
    {href:'/user/profile',icon:'user',label:'Profile',key:'upro'},
    {href:'/admin/settings',icon:'settings',label:'Settings',key:'uset'}
  ];
  var STAFF_NAV=[
    {href:'/admin/dashboard',icon:'home',label:'Dashboard',key:'sd'},
    {label:'OPERATIONS'},
    {href:'/admin/booking-management',icon:'bookmark',label:'Bookings',key:'ab'},
    {href:'/admin/check-ins',icon:'key',label:'Check-ins',key:'sci'},
    {href:'/admin/check-outs',icon:'logout',label:'Check-outs',key:'sco'},
    {href:'/admin/room-management',icon:'bed',label:'Rooms',key:'ar'},
    {href:'/admin/user-management',icon:'users',label:'Guests',key:'sgu'},
    {label:'GUEST SERVICES'},
    {href:'/admin/food-management',icon:'utensils',label:'Food & Services',key:'af'},
    {href:'/admin/food-orders',icon:'inbox',label:'Food Orders',key:'afo'},
    {href:'/admin/wifi-management',icon:'wifi',label:'WiFi',key:'aw'},
    {label:'PAYMENTS'},
    {href:'/admin/payment-management',icon:'card',label:'Payments',key:'ap'},
    {label:'ACCOUNT'},
    {href:'/admin/profile',icon:'user',label:'Profile',key:'apro'},
    {href:'/admin/settings',icon:'settings',label:'Settings',key:'aset'}
  ];
  var ADMIN_NAV=[
    {href:'/admin/dashboard',icon:'home',label:'Dashboard',key:'ad'},
    {label:'MANAGEMENT'},
    {href:'/admin/booking-management',icon:'bookmark',label:'Bookings',key:'ab'},
    {href:'/admin/room-management',icon:'bed',label:'Rooms',key:'ar'},
    {href:'/admin/user-management',icon:'users',label:'Guests',key:'au'},
    {href:'/admin/payment-management',icon:'card',label:'Payments',key:'ap'},
    {href:'/admin/promo-management',icon:'tag',label:'Promo Codes',key:'apc'},
    {href:'/admin/discount-management',icon:'gift',label:'Discounts & Offers',key:'ado'},
    {href:'/admin/gallery-management',icon:'image',label:'Gallery',key:'ag'},

    {href:'/admin/wifi-management',icon:'wifi',label:'WiFi',key:'aw'},
    {href:'/admin/food-management',icon:'utensils',label:'Food & Services',key:'af'},
    {href:'/admin/food-orders',icon:'inbox',label:'Food Orders',key:'afo'},
    {href:'/admin/content-management',icon:'edit',label:'Hotel Content',key:'ac'},
    {href:'/admin/reports',icon:'bar',label:'Reports',key:'ar2'},
    {label:'SYSTEM'},
    {href:'/admin/admin-management',icon:'shield',label:'Admin Accounts',key:'aadm',admin:true},
    {href:'/admin/analytics',icon:'activity',label:'Analytics',key:'aal'},
    {href:'/admin/settings',icon:'settings',label:'Settings',key:'aset'},
    {href:'/admin/profile',icon:'user',label:'Profile',key:'apro'}
  ];
  function renderSiteNav(active){
    var c=getFilesContents();
    var nav=$$('nav[data-site-nav]'); if(!nav.length)return;
    nav.forEach(function(n){
      var cur=getCurrentUser();
      var links=PUBLIC_NAV.map(function(l){
        return '<a href="'+l.href+'" class="'+(active===l.key?'active':'')+'">'+l.label+'</a>';
      }).join('');
      if(cur===null||cur===undefined){
        links+='<a href="/login" class="nav-auth-mob">Sign in</a><a href="/register" class="nav-auth-mob">Register</a>';
      }else{
        links+='<a href="'+redirectFor(cur.role)+'" class="nav-auth-mob">My Dashboard</a><a href="/user/profile" class="nav-auth-mob">Profile</a><a href="javascript:void(0)" class="nav-auth-mob" data-mobile-logout>Log out</a>';
      }
      var favsCount=favs().length;
      var right='';
      right+='<button class="icon-btn" data-theme-toggle data-theme-icon title="Toggle theme">'+icon(getTheme()==='dark'?'sun':'moon','icon')+'</button>';
      if(cur){
        var dest=redirectFor(cur.role);
        var drop='<div class="dd" data-dd>'+
            '<div class="user-chip">'+avatarHTML(cur)+'<div><div class="uname">'+esc(cur.name)+'</div><div class="urole"><span class="role-chip">'+esc(roleLabel(cur.role))+'</span></div></div>'+icon('chevDown','icon-sm')+'</div>'+
            '<div class="dd-menu">'+
            '<div class="dd-head">'+avatarHTML(cur,'avatar lg')+'<div><div style="font-weight:650">'+esc(cur.name)+'</div><div class="text-sm text-muted">'+esc(cur.email)+'</div></div></div>'+
            '<a href="'+dest+'">'+icon('home','icon')+' My Dashboard</a>'+
            '<a href="/user/profile">'+icon('user','icon')+' My Profile</a>'+
            '<a href="/user/favorites">'+icon('heart','icon')+' Favorites'+(favsCount?' <span class="count" style="margin-left:auto;font-size:.7rem;background:var(--surface-3);color:var(--muted);padding:.06rem .45rem;border-radius:99px">'+favsCount+'</span>':'')+'</a>'+
            '<div class="divider"></div>'+
            '<button class="danger" data-logout>'+icon('logout','icon')+' Log out</button>'+
            '</div></div>';
        right+=drop;
      }else{
        right+='<a href="/login" class="btn btn-ghost hide-sm">Log in</a><a href="/register" class="btn btn-primary btn-sm">Create account</a>';
      }
      right+='<button class="icon-btn burger" data-burger data-site-burger title="Menu">'+icon('menu','icon')+'</button>';
      n.innerHTML='<div class="customers"; style="display:none"></div>' +
          '<div class="container site-bar-in" style="justify-content:flex-start">'+
          '<a href="/" class="logo"><span class="mark">'+icon('feather','icon')+'</span><span>'+esc(c.hotelName)+'<span class="brand-small">Resort &amp; Spa</span></span></a>'+
          '<nav class="nav-links" data-mobile-nav>'+links+'</nav>'+
          '<div class="bar-actions">'+right+'</div></div>';
    });
    $$('nav[data-site-nav]').forEach(function(n){
      var b=n.querySelector('[data-site-burger]');
      if(b)b.addEventListener('click',function(){
        var ml=n.querySelector('[data-mobile-nav]');
        ml.classList.toggle('mobile-open');
      });
      var lo=n.querySelector('[data-mobile-logout]');
      if(lo)lo.addEventListener('click',function(){ doLogout(); });
    });
  }
  function roleLabel(r){ return r==='SUPER_ADMIN'?'Super Admin':(r==='ADMIN'?'Admin':(r==='STAFF'?'Staff':'Guest')); }
  function renderPublicChrome(active){
    renderSiteNav(active);
    var slot=$('#footslot');
    if(slot){
      var c=getFilesContents();
      slot.innerHTML='<div class="site-foot-wrap"><div class="container">'+
          '<div class="fgrid">'+
          '<div><div class="row" style="gap:.6rem"><span class="mark logo" style="width:36px;height:36px;border-radius:10px;background:linear-gradient(135deg,var(--primary),#0a3a37);color:#fff;display:inline-flex;align-items:center;justify-content:center">'+icon('feather','icon')+'</span><span class="font-display" style="font-size:1.1rem;font-weight:700">'+esc(c.hotelName)+'</span></div>'+
          '<p class="brand-desc">'+esc(c.footerAbout)+'</p>'+
          '<div class="socials mt-3"><a href="'+c.socials.facebook+'" title="Facebook">'+icon('share','icon')+'</a><a href="'+c.socials.instagram+'" title="Instagram">'+icon('camera','icon')+'</a><a href="'+c.socials.twitter+'" title="X">'+icon('moon','icon')+'</a><a href="'+c.socials.youtube+'" title="YouTube">'+icon('play','icon')+'</a></div></div>'+
          '<div><h4>Explore</h4><ul><li><a href="/rooms">Our Rooms</a></li><li><a href="/offers">Offers & Deals</a></li><li><a href="/about">About Us</a></li><li><a href="/rooms">Gallery</a></li><li><a href="/contact">Contact</a></li></ul></div>'+
          '<div><h4>Services</h4><ul><li><a href="/rooms?type=Suite">Suites</a></li><li><a href="/rooms?type=Family">Family Rooms</a></li><li><a href="/rooms?type=Premium">Premium</a></li><li><a href="/about">Spa & Wellness</a></li><li><a href="/about">Dining</a></li></ul></div>'+
          '<div><h4>Contact</h4><ul>'+
          '<li><a href="/contact" class="row" style="gap:.5rem">'+icon('pin','icon-sm')+esc(c.address)+'</a></li>'+
          '<li><a href="tel:'+c.phone+'" class="row" style="gap:.5rem">'+icon('phone','icon-sm')+esc(c.phone)+'</a></li>'+
          '<li><a href="mailto:'+c.email+'" class="row" style="gap:.5rem">'+icon('mail','icon-sm')+esc(c.email)+'</a></li>'+
          '<li><span class="row text-sm" style="gap:.5rem">'+icon('clock','icon-sm')+'<span>'+esc(c.supportHours)+'</span></span></li>'+
          '</ul></div></div>'+
          '<div class="foot-bottom"><span>&copy; '+new Date().getFullYear()+' '+esc(c.hotelName)+'. All rights reserved.</span><span>Hotel Booking Management System &mdash; Frontend Prototype</span></div>'+
          '</div></div>';
      if(!SVGS.play) SVGS.play='<path d="m6 3 14 9-14 9z"/>';
      $$('a[title="YouTube"]').forEach(function(a){ a.innerHTML=icon('play','icon'); });
    }
  }
  function sidebarNav(active,role){
    var cur=getCurrentUser();
    var items;
    if(role==='STAFF') items=STAFF_NAV;
    else if(role==='ADMIN'||role==='SUPER_ADMIN') items=ADMIN_NAV;
    else items=USER_NAV;
    var out='';
    items.forEach(function(it){
      if(it.label&&!it.href){
        if(it.admin&&role!=='SUPER_ADMIN')return;
        out+='<div class="sb-group"><div class="sb-label">'+it.label+'</div></div>'; return;
      }
      if(it.admin&&role!=='SUPER_ADMIN')return;
      var count='';
      if(it.count==='favs'){ var f=favs().length; if(f)count='<span class="count">'+f+'</span>'; }
      out+='<a href="'+it.href+'" class="sb-link '+(it.key===active?'active':'')+'">'+icon(it.icon,'icon')+'<span>'+it.label+'</span>'+count+'</a>';
    });
    return out;
  }
  function topbarHTML(title,crumb){
    var cur=getCurrentUser();
    var dest=redirectFor(cur.role);
    var notifs=LS.get('hv_notifs',[]); var unread=notifs.filter(function(n){return !n.read;}).length;
    var crumbHtml=crumb?crumb.map(function(x,i){
      if(i===crumb.length-1)return '<span class="cur">'+esc(x.label)+'</span>';
      return '<a href="'+(x.href||'javascript:void(0)')+'">'+esc(x.label)+'</a><span class="sep">/</span>';
    }).join(''):'';
    var drop=''+
        '<div class="dd" data-dd>'+
        '<div class="user-chip">'+avatarHTML(cur)+'<div><div class="uname">'+esc(cur.name)+'</div><div class="urole"><span class="role-chip">'+esc(roleLabel(cur.role))+'</span></div></div>'+icon('chevDown','icon-sm')+'</div>'+
        '<div class="dd-menu">'+
        '<div class="dd-head">'+avatarHTML(cur,'avatar lg')+'<div><div style="font-weight:650">'+esc(cur.name)+'</div><div class="text-sm text-muted">'+esc(cur.email)+'</div></div></div>'+
        '<a href="'+dest+'">'+icon('home','icon')+' Dashboard</a>'+
        '<a href="'+(cur.role==='GUEST'?'/user/profile':'/admin/profile')+'">'+icon('user','icon')+' My Profile</a>'+
        '<a href="/admin/settings">'+icon('settings','icon')+' Settings</a>'+
        (cur.role!=='GUEST'?'<a href="/">'+icon('globe','icon')+' View Website</a>':'')+
        '<div class="divider"></div>'+
        '<button class="danger" data-logout>'+icon('logout','icon')+' Log out</button>'+
        '</div></div>';
    return '<button class="icon-btn burger" data-burger title="Menu">'+icon('menu','icon')+'</button>'+
        '<div class="tb-title hide-sm">'+esc(title)+'</div>'+
        '<nav class="tb-breadcrumb" aria-label="Breadcrumb">'+crumbHtml+'</nav>'+
        '<div class="tb-right">'+
        '<button class="icon-btn" data-theme-toggle data-theme-icon title="Toggle theme">'+icon(getTheme()==='dark'?'sun':'moon','icon')+'</button>'+
        '<button class="icon-btn" onclick="location.href=\''+(cur.role==='GUEST'?'/user/notifications':'/admin/notifications')+'\'" title="Notifications">'+icon('bell','icon')+(unread?'<span class="dot"></span>':'')+'</button>'+
        drop+'</div>';
  }
  function renderDashChrome(active,title,crumb){
    var u=getCurrentUser(); var role=u?u.role:'GUEST';
    var sb=$('#sidebar');
    if(sb){
      sb.innerHTML='<div class="sb-logo"><span class="mark logo" style="width:36px;height:36px;border-radius:10px;background:linear-gradient(135deg,var(--primary),#0a3a37);color:#fff;display:inline-flex;align-items:center;justify-content:center">'+icon('feather','icon')+'</span><div><div style="font-weight:800;font-size:.95rem;line-height:1.1">'+esc(getFilesContents().hotelName)+'</div><div class="text-xs" style="color:var(--muted);letter-spacing:.14em;text-transform:uppercase;font-weight:700">'+esc(roleLabel(role))+'</div></div></div>'+
          '<div class="sb-scroll">'+sidebarNav(active,role)+'</div>'+
          '<div class="sb-footer"><a href="'+(role==='GUEST'?'/user/profile':'/admin/profile')+'" class="sb-user">'+avatarHTML(u,'avatar sm')+'<div><div class="uname">'+esc(u.name)+'</div><div class="urole">'+esc(roleLabel(role))+'</div></div></a>'+
          '<button class="sb-logout" data-logout>'+icon('logout','icon')+'<span>Log out</span></button></div>';
    }
    var tb=$('#topbar');
    if(tb){ tb.innerHTML=topbarHTML(title,crumb); }
    var veil=document.createElement('div'); veil.className='sb-veil';
    if(!$('.sb-veil'))document.body.appendChild(veil);
    document.addEventListener('click',function(e){
      var b=e.target.closest('[data-burger]');
      var vsb=document.getElementById('sidebar');
      if(b&&vsb){ sb.classList.toggle('open'); veil.classList.toggle('open'); document.body.style.overflow=sb.classList.contains('open')?'hidden':''; }
      if(e.target.classList&&e.target.classList.contains('sb-veil')){ vsb.classList.remove('open'); veil.classList.remove('open'); document.body.style.overflow=''; }
    });
  }
  function wireDD(){
    document.addEventListener('click',function(e){
      var dd=e.target.closest('[data-dd]');
      $$('.dd.open').forEach(function(d){ if(d!==dd)d.classList.remove('open'); });
      if(dd){ e.stopPropagation(); dd.classList.toggle('open'); }
    });
    document.addEventListener('click',function(e){
      var lg=e.target.closest('[data-logout]');
      if(lg){ e.preventDefault(); e.stopPropagation(); doLogout(); }
    });
  }
  function doLogout(){
    try{ localStorage.removeItem(CURRENT_USER_KEY); }catch(e){}
    clearAuthCookie();
    var f=document.createElement('form'); f.method='POST'; f.action='/logout'; f.style.display='none';
    document.body.appendChild(f); f.submit();
  }
  function hydrateIcons(root){
    (root||document).querySelectorAll('[data-icon]').forEach(function(el){ el.innerHTML=icon(el.getAttribute('data-icon'), el.getAttribute('data-icon-class')||'icon'); });
  }
  function hydrateAvatars(root){
    (root||document).querySelectorAll('[data-avatar]').forEach(function(el){
      var who=el.getAttribute('data-avatar');
      var usr=null;
      if(who==='current') usr=getCurrentUser();
      else if(who) usr=userById(who);
      if(!usr) usr={name:el.getAttribute('data-name')||'Guest',role:el.getAttribute('data-role')||'GUEST'};
      el.innerHTML=avatarHTML(usr, el.getAttribute('data-class')||'avatar');
    });
  }
  function hydrateAll(){ hydrateIcons(); hydrateAvatars(); }
  function favCounters(){
    $$('[data-favcount]').forEach(function(el){ el.textContent=favs().length; });
  }
  function roomLink(source, roomId){
    return (source==='guest' ? '/user/room-details?id=' : '/room-details?id=') + roomId;
  }

  // Guest WiFi is one real hotel-wide network (WifiConfig, managed from
  // Admin > WiFi Management) - like almost every real hotel, there's no
  // separate password per room or floor. Any page that shows a guest their
  // WiFi details must supply window.__SERVER_WIFI__ (see
  // GuestDashboardController, GuestBookingController, AdminController).
  function wifiForBooking(b){
    var hotel=(window.__SERVER_WIFI__)||{ssid:'HotelWiFi',password:'Welcome123',instructions:'Connect to the network shown and enter the password.'};
    var room=b?roomById(b.roomId):null;
    return {ssid:hotel.ssid,password:hotel.password,instructions:hotel.instructions,label:room?('Guest WiFi \u2022 '+room.name):'Guest WiFi'};
  }

  // Draws a real, scannable WiFi QR code using QRCode.js.
  // The library must be loaded before core.js from /js/qrcode.min.js.
  function drawWifiQR(canvasId,ssid,password){
    var el=document.getElementById(canvasId); if(!el)return;
    if(typeof QRCode==='undefined'){
      // Keep the existing placeholder if the QR library is unavailable.
      return;
    }
    if(!ssid||!password)return;

    var parent=el.parentNode; if(!parent)return;
    var size=(el.tagName==='CANVAS'?el.width:el.offsetWidth)||220;

    // QRCode.js renders into a div. Replace the old canvas/div so repeated
    // renders, such as live WiFi preview updates, remain safe.
    var holder=document.createElement('div');
    holder.id=canvasId;
    holder.style.width=size+'px';
    holder.style.height=size+'px';
    parent.replaceChild(holder,el);

    var data='WIFI:T:WPA;S:'+ssid+';P:'+password+';;';
    try{
      new QRCode(holder,{
        text:data,
        width:size,
        height:size,
        colorDark:'#0d3a36',
        colorLight:'#ffffff',
        correctLevel:QRCode.CorrectLevel.M
      });
    }catch(e){
      // Do not break the rest of the page if QR rendering fails.
    }
  }

  // Auto-shown when a booking's room flips to ACTIVE (checked in). Session-scoped
  // per booking id (sessionStorage) so a guest isn't shown the same popup on
  // every page load/poll, and per-browser-session so it correctly handles
  // multiple guests each on their own device/session.
  function showWifiWelcomePopup(booking){
    var seenKey='hv_wifi_popup_'+booking.id;
    if(sessionStorage.getItem(seenKey))return;
    sessionStorage.setItem(seenKey,'1');
    var w=wifiForBooking(booking);
    var m=openModal(
        '<div class="modal-head"><h3>'+icon('wifi','icon')+' You\'re checked in!</h3><button class="modal-x" aria-label="Close">&times;</button></div>'+
        '<div class="modal-body" style="text-align:center">'+
        '<p style="color:var(--muted);font-size:.9rem;margin-bottom:1rem">Room '+esc((booking.roomName||'')+'')+' is ready. Scan below to join the guest WiFi instantly.</p>'+
        '<div style="background:#fff;border-radius:12px;padding:1rem;display:inline-block;box-shadow:var(--shadow)"><canvas id="welcomeQr" width="220" height="220"></canvas></div>'+
        '<div style="margin-top:1rem"><div class="text-sm" style="color:var(--muted)">Network</div><div style="font-weight:700;font-size:1.05rem">'+esc(w.ssid)+'</div></div>'+
        '<div style="margin-top:.5rem"><div class="text-sm" style="color:var(--muted)">Password</div><div style="font-weight:700;font-size:1.05rem;letter-spacing:.06em">'+esc(w.password)+'</div></div>'+
        '</div>'+
        '<div class="modal-foot"><a class="btn btn-ghost" href="/user/wifi">Open WiFi page</a><button class="btn btn-primary" data-close="1">Got it</button></div>',
        'sm'
    );
    drawWifiQR('welcomeQr',w.ssid,w.password);
    m.q('[data-close]').addEventListener('click',function(){ m.close(); });
  }

  // Polls the guest's own bookings every 20s and auto-shows the WiFi welcome
  // popup the moment one flips to ACTIVE (front desk checked them in). Each
  // guest's browser only ever polls their own bookings (the endpoint is
  // scoped to the logged-in user server-side), so this naturally handles
  // any number of guests being checked in at the same time, independently.
  function startBookingStatusWatch(){
    var known={};
    (getBookings()||[]).forEach(function(b){ known[b.id]=b.status; });
    function poll(){
      fetch('/user/bookings/live-status',{headers:{'Accept':'application/json'}})
          .then(function(r){ return r.ok?r.json():[]; })
          .then(function(rows){
            (rows||[]).forEach(function(row){
              var was=known[row.id];
              known[row.id]=row.status;
              if(row.status==='active' && was && was!=='active'){
                var full=(getBookings()||[]).filter(function(b){return String(b.id)===String(row.id);})[0]
                    || {id:row.id,roomId:row.roomId,roomName:row.roomName};
                showWifiWelcomePopup(full);
              }
            });
          })
          .catch(function(){ /* silent - this is a background convenience poll, not critical */ });
    }
    setInterval(poll,20000);
  }

  window.$ = $;
  window.$$ = $$;
  window.App = {
    LS:LS, icon:icon, stars:stars, esc:esc, cap:cap, ph:ph, IMG:IMG,
    addDays:addDays, fmtDate:fmtDate, fmtDateLong:fmtDateLong, fmtDateISO:fmtDateISO, todayISO:todayISO, parseISO:parseISO, nights:nights, money:money, fmtWhen:fmtWhen,
    getRooms:getRooms, saveRooms:saveRooms, getUsers:getUsers, saveUsers:saveUsers, getBookings:getBookings, saveBookings:saveBookings,
    getPromotions:getPromotions, savePromotions:savePromotions, getPromoCodes:getPromoCodes, savePromoCodes:savePromoCodes,
    getDemoUsers:getDemoUsers,
    getGallery:getGallery, saveGallery:saveGallery, getFood:getFood, saveFood:saveFood, getNotifs:getNotifs, saveNotifs:saveNotifs, addNotif:addNotif, syncBookingNotifs:syncBookingNotifs,
    getContents:getContents, saveContents:saveContents, getWifi:getWifi, saveWifi:saveWifi, getSettings:getSettings, saveSettings:saveSettings,
    getOrders:getOrders, saveOrders:saveOrders, getSearch:getSearch, saveSearch:saveSearch,
    roomById:roomById, userById:userById, bookingById:bookingById, activePromo:activePromo, getActivePromos:getActivePromos,
    getCurrentUser:getCurrentUser, setCurrentUser:setCurrentUser, login:login, register:register, updateProfile:updateProfile,
    readImgFile:readImgFile, logout:logout,
    favs:favs, saveFavs:saveFavs, isFav:isFav, toggleFav:toggleFav,
    bookNo:bookNo, promoResult:promoResult, priceBreakdown:priceBreakdown, createBooking:createBooking,
    toast:toast, openModal:openModal, openConfirm:openConfirm, fieldErrors:fieldErrors, clearErrors:clearErrors,
    month:month, dateStr:dateStr, shortDate:shortDate, toInput:toInput, toTs:toTs, openForm:openForm,
    boot:boot, renderPublicChrome:renderPublicChrome, renderDashChrome:renderDashChrome, wireDD:wireDD,
    roleLabel:roleLabel, initials:initials, avatarHTML:avatarHTML,
    hydrateAll:hydrateAll, hydrateIcons:hydrateIcons,
    $:$ , $$:$$, debounce:debounce, emptyState:emptyState,
    skeletonCard:skeletonCard, skeletonRows:skeletonRows, imgTag:imgTag, pagination:pagination, initPager:initPager,
    Charts:Charts, today:today, ROOM_STATUS:ROOM_STATUS, AMENITIES:AMENITIES, TAX_RATE:TAX_RATE, wifiForBooking:wifiForBooking,
    drawWifiQR:drawWifiQR, startBookingStatusWatch:startBookingStatusWatch,
    liveStay:liveStay, stayProgress:stayProgress, countdown:countdown, fmtCountdown:fmtCountdown, doCheckIn:doCheckIn, doCheckOut:doCheckOut,
    bookingCheckInDT:bookingCheckInDT, bookingCheckOutDT:bookingCheckOutDT, STAY_STATES:STAY_STATES
  };
})();