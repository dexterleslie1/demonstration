const OpenCC = require('opencc-js');

// 简体 -> 繁体 (cn -> tw)
const s2t = OpenCC.Converter({ from: 'cn', to: 'tw' });
const simplified = '汉字转换示例：软件、鼠标、内存';
const traditional = s2t(simplified);
console.log('简体 -> 繁体:');
console.log('  输入:', simplified);
console.log('  输出:', traditional);

console.log('');

// 繁体 -> 简体 (tw -> cn)
const t2s = OpenCC.Converter({ from: 'tw', to: 'cn' });
const traditionalInput = '漢字轉換示例：軟件、鼠標、內存';
const simplifiedOutput = t2s(traditionalInput);
console.log('繁体 -> 简体:');
console.log('  输入:', traditionalInput);
console.log('  输出:', simplifiedOutput);
