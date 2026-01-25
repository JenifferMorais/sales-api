const fs = require('fs');
const path = require('path');

const requiredStructure = {
  'src/app/core/guards': ['auth.guard.ts'],
  'src/app/core/interceptors': ['auth.interceptor.ts', 'loader.interceptor.ts'],
  'src/app/layout/header': [],
  'src/app/layout/header-profile': [],
  'src/app/layout/sidebar': [],
  'src/app/shared/components/alert': [],
  'src/app/shared/components/button': [],
  'src/app/shared/components/data-table-outline': [],
  'src/app/shared/components/filter-bar': [],
  'src/app/shared/components/loader': [],
  'src/app/shared/components/modal': [],
  'src/app/shared/services/alert': [],
  'src/app/shared/services/loader': [],
  'src/app/shared/services/theme': [],
  'src/app/shared/utils': [],
  'src/app/private/dashboard': [],
  'src/app/private/customers': [],
  'src/app/private/products': [],
  'src/app/private/sales': [],
  'src/app/private/reports': [],
  'src/app/public/auth': []
};

console.log('🔍 Validando estrutura do projeto...\n');

let missingDirs = [];
let missingFiles = [];

for (const [dir, files] of Object.entries(requiredStructure)) {
  const fullPath = path.join(__dirname, dir);

  if (!fs.existsSync(fullPath)) {
    missingDirs.push(dir);
    console.log(`❌ Pasta ausente: ${dir}`);
  } else {
    console.log(`✅ Pasta OK: ${dir}`);

    for (const file of files) {
      const filePath = path.join(fullPath, file);
      if (!fs.existsSync(filePath)) {
        missingFiles.push(`${dir}/${file}`);
        console.log(`   ⚠️  Arquivo ausente: ${file}`);
      } else {
        console.log(`   ✅ Arquivo OK: ${file}`);
      }
    }
  }
}

console.log('\n📊 Resumo:');
console.log(`Pastas criadas: ${Object.keys(requiredStructure).length - missingDirs.length}/${Object.keys(requiredStructure).length}`);

if (missingDirs.length === 0 && missingFiles.length === 0) {
  console.log('\n🎉 Estrutura completa e correta!');
  process.exit(0);
} else {
  console.log('\n⚠️  Estrutura incompleta');
  if (missingDirs.length > 0) {
    console.log(`\nPastas faltando (${missingDirs.length}):`);
    missingDirs.forEach(d => console.log(`  - ${d}`));
  }
  if (missingFiles.length > 0) {
    console.log(`\nArquivos faltando (${missingFiles.length}):`);
    missingFiles.forEach(f => console.log(`  - ${f}`));
  }
  process.exit(1);
}
