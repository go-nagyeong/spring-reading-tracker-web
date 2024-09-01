import {
    ClassicEditor,
    AccessibilityHelp,
    Alignment,
    AutoLink,
    Autosave,
    BlockQuote,
    Bold,
    Essentials,
    Heading,
    HorizontalLine,
    Indent,
    IndentBlock,
    Italic,
    Link,
    Paragraph,
    Strikethrough,
    Underline,
    Undo
} from 'ckeditor5';
import translations from 'ckeditor5/translations/ko.js';

const ckEditorConfig = {
    toolbar: {
        items: [
            'undo',
            'redo',
            '|',
            'heading',
            '|',
            'bold',
            'italic',
            'underline',
            'strikethrough',
            '|',
            'horizontalLine',
            'link',
            'blockQuote',
            '|',
            'alignment',
            '|',
            'indent',
            'outdent',
            '|',
            'accessibilityHelp'
        ],
        shouldNotGroupWhenFull: false
    },
    plugins: [
        AccessibilityHelp,
        Alignment,
        AutoLink,
        Autosave,
        BlockQuote,
        Bold,
        Essentials,
        Heading,
        HorizontalLine,
        Indent,
        IndentBlock,
        Italic,
        Link,
        Paragraph,
        Strikethrough,
        Underline,
        Undo
    ],
    heading: {
        options: [
            {
                model: 'paragraph',
                title: 'Paragraph',
                class: 'ck-heading_paragraph'
            },
            {
                model: 'heading1',
                view: 'h1',
                title: 'Heading 1',
                class: 'ck-heading_heading1'
            },
            {
                model: 'heading2',
                view: 'h2',
                title: 'Heading 2',
                class: 'ck-heading_heading2'
            },
            {
                model: 'heading3',
                view: 'h3',
                title: 'Heading 3',
                class: 'ck-heading_heading3'
            },
            {
                model: 'heading4',
                view: 'h4',
                title: 'Heading 4',
                class: 'ck-heading_heading4'
            },
            {
                model: 'heading5',
                view: 'h5',
                title: 'Heading 5',
                class: 'ck-heading_heading5'
            },
            {
                model: 'heading6',
                view: 'h6',
                title: 'Heading 6',
                class: 'ck-heading_heading6'
            }
        ]
    },
    initialData: '',
    link: {
        addTargetToExternalLinks: true,
        defaultProtocol: 'https://',
        decorators: {
            toggleDownloadable: {
                mode: 'manual',
                label: 'Downloadable',
                attributes: {
                    download: 'file'
                }
            }
        }
    },
    placeholder: '',
    language: 'ko',
    translations: [translations]
};


export class CKEditor {
    static instance = null;
    static promise = null;

    constructor(idProperty) {
        if (CKEditor.instance) {
            CKEditor.instance.destroy();
        }
        CKEditor.promise = ClassicEditor.create(document.getElementById(idProperty), ckEditorConfig)
            .then(editor => {
                CKEditor.instance = editor;
            });
    }

    async fill(content) {
        await CKEditor.promise;
        CKEditor.instance.setData(content);
    }

    save() {
        try {
            return CKEditor.instance.getData();
        } catch (error) {
            console.error('Error getting content:', error);
        }
    }
}

export class SimpleEditor {
    static instance = null;

    constructor(idProperty) {
        if (SimpleEditor.instance) {
            SimpleEditor.instance.destroy();
        }
        SimpleEditor.instance = new EditorJS(idProperty);
    }

    async fill(content) {
        await SimpleEditor.instance.isReady;
        this.convertHtmlToData(content).map(data => {
            SimpleEditor.instance.blocks.insert('paragraph', { text: data });
        })
    }

    async save() {
        try {
            const outputData = await SimpleEditor.instance.save();
            return this.convertDataToHtml(outputData.blocks)
        } catch (error) {
            console.error('Error getting content:', error);
        }
    }

    convertHtmlToData(htmlString) {
        const lastIndex = htmlString.lastIndexOf('</p>');
        return htmlString.substring(0, lastIndex).replaceAll('<p>','').split('</p>');
    }

    convertDataToHtml(blocks) {
        let convertedHtml = '';
        blocks.map(block => {
            switch (block.type) {
                case 'paragraph':
                    convertedHtml += `<p>${block.data.text}</p>`;
                    break;
                default:
                    console.log('Unknown block type', block.type);
                    break;
            }
        });
        return convertedHtml;
    }
}